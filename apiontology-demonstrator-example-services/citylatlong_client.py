'''
  service test client returns for a given city and adminarea the corresponding lat,long
  @author a.both
'''
from pysimplesoap.client import SoapClient, SoapFault

port = 8182
servicename = 'CityLatLong'

# create a simple consumer
client = SoapClient(
    location = "http://localhost:%d/" % (port,),
    action = 'http://localhost:%d/'  % (port,), # SOAPAction
    namespace = "http://example.com/%s.wsdl" % (servicename,), 
    soap_ns='soap',
    ns = False)

# Test: call the remote method
def test(cityname, adminarea):
  response = client.CityLatLong(cityname=cityname,adminarea=adminarea)
  try:
    result="lat=%s, long=%s" % (response("lat"),response("long"))
  except:
    result="no data"

  print "cityname=%s, adminarea=%s -> %s" % (cityname,adminarea,result)

test('New York City','United States')
test('Bangalore','India')
