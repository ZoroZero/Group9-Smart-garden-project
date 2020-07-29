import random

from flask import Flask, request, jsonify

import numpy as np
from statsmodels.tsa.holtwinters import ExponentialSmoothing
import traces as traces

from pandas import datetime
app = Flask(__name__)


# root
@app.route("/")
def index():
    """
    this is a root dir of my server
    :return: str
    """
    return "This is root!!!!"


# GET
@app.route('/users/<user>')
def hello_user(user):
    """
    this serves as a demo purpose
    :param user:
    :return: str
    """
    return "Hello %s!" % user


# POST
@app.route('/api/post_some_data', methods=['POST'])
def get_text_prediction():
    """
    predicts requested text whether it is ham or spam
    :return: json
    """
    jsons = request.get_json()
            
    time_data = traces.TimeSeries()
    for key in jsons.keys():
        time_data[parse_iso_datetime(key)] = jsons[key]

    result = get_result(time_data)
    return jsonify({'result': result})

def check_seasonal_time(x):
    result_list = set()
    for i in range(x.n_measurements()-1):
        item = abs(x.items()[i][0] - x.items()[i+1][0]).total_seconds()
        result_list.add(item)
    if len(result_list) == 1:
        return True
    else:
        test = np.array(list(result_list))
        std = np.std(test)
        if std < 1:
            return True
        else:
            return False

def check_seasonal_value(x):
    signal_list = []
    for i in range(x.n_measurements()):
        item = x.items()[i][1]
        signal_list.append(item)
    signal = np.asarray(signal_list,dtype=float)

    check_auto = autocorr(signal)
    check_crossing = freq_from_crossings(signal)
    return (check_auto and (check_crossing)), signal

def autocorr(x):
    n = x.size
    if (n == 2):
        return x[0] == x[1]
    norm = (x - np.mean(x))
    result = np.correlate(norm, norm, mode='same')
    acorr = result[n//2 + 1:] / (x.var() * np.arange(n-1, n//2, -1))
    lag = np.abs(acorr).argmax() + 1
    r = acorr[lag-1]
    if np.abs(r) > 0.7:
      return True
    else:
      return False


def freq_from_crossings(sig):
    """
    Estimate frequency by counting zero crossings
    """
    # Find all indices right before a rising-edge zero crossing
    mean = np.mean(sig)
    indices = np.nonzero((sig[1:] >= mean) & (sig[:-1] < mean))[0]

    if len(indices) == 0:
        return False

    crossings = [i - sig[i] / (sig[i+1] - sig[i]) for i in indices]

    # Some other interpolation based on neighboring points might be better.
    # Spline, cubic, whatever
    #return np.mean(np.diff(crossings))/fs
    return True
def check_seasonal_data(x):
    check_value, signal = check_seasonal_value(x)
    check_time  = check_seasonal_time(x)
    return (check_time and check_value),signal

def get_result(t_data):

    if (t_data.n_measurements() == 0):
        return 0
    if (t_data.n_measurements() == 1):
        return t_data.items()[0][1]

    check_seasonal,signal = check_seasonal_data(t_data)
    nearest_value = np.mean(signal)
    std = np.std(signal)
    if check_seasonal:
        return np.max(signal)
    else:
        time_diff = abs(t_data.first_key() - t_data.last_key())
        check_time = time_diff.total_seconds()
        if(check_time <= 3600):
            sampling_period  = 1
        elif(check_time > 3600 and check_time <= 86400):
            sampling_period = 10
        elif(check_time > 86400 and check_time <= 800000):
            sampling_period = 100
        elif(check_time > 800000 and check_time <= 2419200):
            sampling_period = 300
        elif(check_time > 2419200 and check_time <= 10000000):
            sampling_period = 1000
        else:
            sampling_period = 3000


        regular = t_data.moving_average(sampling_period, pandas=True)
        random_walk = (int)(sampling_period/t_data.n_measurements())
        for i in range(0,random_walk):
            last_key = regular.keys()[-1]
            from datetime import timedelta
            new_key = last_key+ timedelta(seconds = sampling_period)
            regular[new_key] = nearest_value + np.random.uniform(-0.25,0.25)
            new_last_key = regular.keys()[-1]



        # fit model
        model = ExponentialSmoothing(regular,trend="add",damped= True)
        model_fit = model.fit()
        # make prediction
        yhat = model_fit.predict(len(regular), len(regular) + 90)
        df = yhat.to_frame().reset_index()
        df = df.rename(columns= {'index': 'date',0: 'value'})
        final_result = np.max(df['value'].to_numpy())
        return round(abs(final_result),2)


def parse_iso_datetime(value):
    return datetime.strptime(value, "%Y-%m-%d %H:%M:%S")



# running web app in local machine
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)