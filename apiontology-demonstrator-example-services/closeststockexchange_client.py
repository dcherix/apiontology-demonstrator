'''
  service test client for computing the closest stock exchange for a stocksymbol, latitude and longitude
  @author a.both
'''
from pysimplesoap.client import SoapClient, SoapFault

port = 8185
servicename = 'ClosestStockExchange'

# create a simple consumer
client = SoapClient(
    location = "http://localhost:%d/" % (port,),
    action = 'http://localhost:%d/'  % (port,), # SOAPAction
    namespace = "http://example.com/%s.wsdl" % (servicename,), 
    soap_ns='soap',
    ns = False)

# Test: call the remote method
def test(stocksymbol,latitude,longitude):
  response = client.ClosestStockExchange(stocksymbol=stocksymbol,latitude=latitude,longitude=longitude)
  try:
    result="closestStockExchange=%s, closestStockExchange_lat=%s, closestStockExchange_long=%s" % (response("closestStockExchange"), response("closestStockExchange_lat"), response("closestStockExchange_long"))
  except:
    result="no data"

  print "stocksymbol=%s; latitude=%s; longitude=%s -> %s" % (stocksymbol, latitude, longitude, result)

test( 'SAP', '8.6', '50.1' )
test( 'IBM', '40', '74' )
