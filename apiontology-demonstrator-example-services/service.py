'''
  service base functionality, defined service configuration and start
  @author a.both
'''
from pysimplesoap.server import SoapDispatcher, SOAPHandler
from BaseHTTPServer import HTTPServer

def init_service(port, servicename, userfunction, args, returns):
  # define service
  dispatcher = SoapDispatcher(
    servicename,
    location = "http://localhost:%d/" % (port,),
    action = 'http://localhost:%d/' % (port,), # SOAPAction
    namespace = "http://example.com/%s.wsdl" % (servicename,), 
    prefix="ns0",
    trace = True,
    ns = True)

  # register the user function
  dispatcher.register_function(servicename, userfunction, returns=returns, args=args )

  # start service
  print("Starting server '%s' on port %i ..." % (servicename,port))
  httpd = HTTPServer(("", port), SOAPHandler)
  httpd.dispatcher = dispatcher
  httpd.serve_forever()
