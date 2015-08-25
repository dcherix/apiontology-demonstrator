'''
  company data service 
  @author a.both
'''
from service import init_service
import csv

companies = {}

def companydata( company ):
  '''
    returns the companys city, country and stock name as strings for a given company name
  '''
  global companies
  if companies.has_key(company):
    result = companies[company]
  else:
    result = {}
  print "in: ",company," out: ",result
  return result



def main():
  
  reader = csv.reader(open('companydata.csv', 'r'))
  global companies
  companies = {}
  for row in reader:
    company,stock,city,country = row
    #print company,stock,city,country
    companies[company] = { 'country':country, 'city':city, 'stock':stock }

  init_service(
      port=8181, 
      servicename='CompanyData',
      userfunction=companydata,
      args={'company':str},
      #returns={'CompanyDataResult': {'city':str, 'country':str , 'stock':str}}
      returns={'city':str, 'country':str , 'stock':str}
  )


if  __name__ =='__main__':
  main()
