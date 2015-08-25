'''
  weather service test client
  @author a.both
'''

from pysimplesoap.client import SoapClient, SoapFault

port = 8183
servicename = 'Weather'

# create a simple consumer
client = SoapClient(
    location = "http://localhost:%d/" % (port,),
    action = 'http://localhost:%d/'  % (port,), # SOAPAction
    namespace = "http://example.com/%s.wsdl" % (servicename,), 
    soap_ns='soap',
    ns = False)

# Test: call the remote method
def test(city, country):
  response = client.Weather(city=city,country=country)
  try:
    result="weather=%s" % (response("weather"),)
  except:
    result="no data"

  print "city=%s, country=%s -> %s" % (city,country,result)

test('New York','United States')
#test('Berlin','Germany')
