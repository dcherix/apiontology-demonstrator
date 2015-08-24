'''
  stock service build on top of markitondemand API
  @author a.both
'''

from service import init_service
import json
import urllib2
import urllib

localcache = {}


def stock( symbol ):
  '''
    returns the weather description one-liner for the given city and country by calling Yahoo weather API
    service contains some basic caching for calling the external service just once per symbol (cache TTL: infinite)
  '''
  try:
    global localcache
    symbolencoded = urllib.quote_plus(symbol)
    # test for cache hit
    if not localcache.has_key(symbolencoded):
      # call Yahoo weather service
      serviceresult = json.load(urllib2.urlopen('http://dev.markitondemand.com/Api/v2/Quote/json?symbol='+symbolencoded))
      # check if there was an error Message by the service
      if serviceresult.has_key('Message'):
	localcache[symbolencoded] = {}
        raise Exception(serviceresult['Message'])
      else:
        # default case: rate and companyname received
        localcache[symbolencoded] = {'rate':serviceresult['LastPrice'], 'companyname':serviceresult['Name']}
    else:
      print('cache hit')
    result = localcache[symbolencoded]
  except Exception as e:
    print e
    result = {}
  print "in: ",symbol," out: ",result
  return result


def main():

  init_service(
      port=8184, 
      servicename='Stock',
      userfunction=stock,
      args={'symbol':str},
      returns={'StockResult': {'rate':str, 'companyname':str}}
  )


if  __name__ =='__main__':
  main()
