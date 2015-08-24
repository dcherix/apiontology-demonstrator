'''
  stock service test client
  @author a.both
'''
from pysimplesoap.client import SoapClient, SoapFault

port = 8184
servicename = 'Stock'

# create a simple consumer
client = SoapClient(
    location = "http://localhost:%d/" % (port,),
    action = 'http://localhost:%d/'  % (port,), # SOAPAction
    namespace = "http://example.com/%s.wsdl" % (servicename,), 
    soap_ns='soap',
    ns = False)

# Test: call the remote method
def test(symbol):
  response = client.Stock(symbol=symbol)
  try:
    result="rate=%s, companyname=%s" % (response("rate"),response("companyname"))
  except:
    result="no data"

  print "symbol=%s -> %s" % (symbol,result)

test('APPL')
test('AAPL')
