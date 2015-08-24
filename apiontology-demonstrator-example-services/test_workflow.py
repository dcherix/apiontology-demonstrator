'''
  python test_workflow.py -company "Google"
  @author a.both
'''

from pysimplesoap.client import SoapClient, SoapFault
import sys, getopt

#company from command line
company = None
if len(sys.argv) == 3:
  if sys.argv[1] == '-company':
    company = sys.argv[2]
    print "company from input: %s" % (company,)
if company == None:
  company = 'Google'


'''
  fetch company data by given company name
'''

port = 8181
servicename = 'CompanyData'
client = SoapClient(
    location = "http://localhost:%d/" % (port,),
    action = 'http://localhost:%d/'  % (port,), # SOAPAction
    namespace = "http://example.com/%s.wsdl" % (servicename,), 
    soap_ns='soap',
    ns = False)
responseCompanyData = client.CompanyData(company=company)
res = {}
print servicename+": company=%s\n  -->  country=%s; city=%s; stock=%s\n" % (company, responseCompanyData('country'), responseCompanyData('city'), responseCompanyData('stock'))


'''
  fetch weather for company location
'''
city = responseCompanyData('city')
country = responseCompanyData('country')

port = 8183
servicename = 'Weather'
client = SoapClient(
    location = "http://localhost:%d/" % (port,),
    action = 'http://localhost:%d/'  % (port,), # SOAPAction
    namespace = "http://example.com/%s.wsdl" % (servicename,), 
    soap_ns='soap',
    ns = False)
responseWeather = client.Weather(city=city,country=country)
print servicename+": city=%s; country=%s\n  -->  weather=%s\n" % (city,country,responseWeather('weather'))


'''
  compute stock
'''
symbol = responseCompanyData('stock')


port = 8184
servicename = 'Stock'
client = SoapClient(
    location = "http://localhost:%d/" % (port,),
    action = 'http://localhost:%d/'  % (port,), # SOAPAction
    namespace = "http://example.com/%s.wsdl" % (servicename,), 
    soap_ns='soap',
    ns = False)
responseStock = client.Stock(symbol=symbol)
print servicename+": symbol=%s\n  -->  rate=%s; companyname=%s\n" % (symbol,responseStock('rate'), responseStock('companyname') )



'''
  compute the lat long of the given cityname and adminarea
'''
cityname = responseCompanyData('city')
adminarea = responseCompanyData('country')

port = 8182
servicename = 'CityLatLong'
client = SoapClient(
    location = "http://localhost:%d/" % (port,),
    action = 'http://localhost:%d/'  % (port,), # SOAPAction
    namespace = "http://example.com/%s.wsdl" % (servicename,), 
    soap_ns='soap',
    ns = False)
responseCityLatLong = client.CityLatLong(cityname=cityname,adminarea=adminarea)
print servicename+": cityname=%s; adminarea=%s\n  -->  lat=%s; long=%s\n" % (cityname,adminarea,responseCityLatLong('lat'), responseCityLatLong('long') )



'''
  ClosestStockExchange
'''
stocksymbol = responseCompanyData('stock')
latitude = responseCityLatLong('lat')
longitude = responseCityLatLong('long') 


port = 8185
servicename = 'ClosestStockExchange'
client = SoapClient(
    location = "http://localhost:%d/" % (port,),
    action = 'http://localhost:%d/'  % (port,), # SOAPAction
    namespace = "http://example.com/%s.wsdl" % (servicename,), 
    soap_ns='soap',
    ns = False)
responseCSE = client.ClosestStockExchange(stocksymbol=stocksymbol,latitude=latitude,longitude=longitude)
print servicename+": stocksymbol=%s; latitude=%s; longitude=%s\n  -->  closestStockExchange=%s; closestStockExchange_lat=%s; closestStockExchange_long=%s\n" % (stocksymbol, latitude, longitude, responseCSE('closestStockExchange'), responseCSE('closestStockExchange_lat'), responseCSE('closestStockExchange_long') )



