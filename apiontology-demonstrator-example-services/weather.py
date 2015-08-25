'''
  weather service, build on top of yahooapis 
  @author a.both
'''
from service import init_service
import csv
from pprint import pprint 
import json
import urllib2
import urllib

localcache = {}


def weather( city, country ):
  '''
    returns the weather description one-liner for the given city and country by calling Yahoo weather API
  '''
  try:
    global localcache
    citycountryencoded = urllib.quote_plus(city)+'%2C%20'+urllib.quote_plus(country)
    # test for cache hit
    if not localcache.has_key(citycountryencoded):
      # call Yahoo weather service
      serviceresult = json.load(urllib2.urlopen('https://query.yahooapis.com/v1/public/yql?q=select%20item.condition.text%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22'+citycountryencoded+'%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys' ))
      weatherline = serviceresult['query']['results']['channel']['item']['condition']['text']
      localcache[citycountryencoded] = weatherline
    else:
      print('cache hit')
    result = {'weather':localcache[citycountryencoded]}
  except Exception as e:
    result = {'weather':'no weather available (%s)' % (e,)}
  print "in: ",city,country," out: ",result
  return result


def main():

  init_service(
      port=8183, 
      servicename='Weather',
      userfunction=weather,
      args={'city':str, 'country':str},
      returns={'WeatherResult': {'weather':str}}
  )


if  __name__ =='__main__':
  main()
  
  
# 