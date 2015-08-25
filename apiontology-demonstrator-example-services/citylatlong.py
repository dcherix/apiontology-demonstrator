'''
  service returns for a given city and adminarea the corresponding lat,long
  @author a.both
'''
from service import init_service
import csv
from pprint import pprint 



def citylatlong( cityname, adminarea ):
  '''
    returns the companys city, country and stock name as strings for a given company name
  '''
  global citylatlong_data
  try:
    result = citylatlong_data[cityname][adminarea] 
  except:
    result = {}
  print("in: ",cityname,adminarea," out: ",result)
  return result


def main():
  # read data from CSV file
  reader = csv.reader(open('citylatlong.csv', 'r'))
  global citylatlong_data
  citylatlong_data = {}
  for row in reader:
    cityname,adminarea,latlong = row
    #print cityname,adminarea,latlong
    long,lat = latlong.split()
    citylatlong_data[cityname] = { adminarea: {'lat':lat, 'long':long} }

  init_service(
      port=8182, 
      servicename='CityLatLong',
      userfunction=citylatlong,
      args={'cityname':str, 'adminarea':str},
      returns={'lat':str, 'long':str}
  )


if  __name__ =='__main__':
  main()