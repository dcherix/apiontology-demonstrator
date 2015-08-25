'''
  company data service test client
  @author a.both
'''
from pysimplesoap.client import SoapClient, SoapFault

port = 8181
servicename = 'CompanyData'

# create a simple consumer
client = SoapClient(
    location = "http://localhost:%d/" % (port,),
    action = 'http://localhost:%d/'  % (port,), # SOAPAction
    namespace = "http://example.com/%s.wsdl" % (servicename,), 
    soap_ns='soap',
    ns = False)

# Test: call the remote method
def test(company):
  response = client.CompanyData(company=company)
  print "%s " % (response.__repr__,)
  try:
    print "company=%s --> city=%s, country=%s, stock=%s" % (company,response("city"),response("stock"),response("country"))
  except:
    print "company=%s --> no data" % (company,)


test('Google')
test('Infosys')
