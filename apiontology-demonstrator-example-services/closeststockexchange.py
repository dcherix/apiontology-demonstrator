from service import init_service
import csv
from pprint import pprint 
import random
import math

stockexchange_data = {}

def closeststockexchange( stocksymbol, latitude, longitude ):
  '''
    returns the stock exchange closest to the given latitude and longitude 
    the actual availability is randomly computed (randomizer seeded by stocksymbol)
  '''
  global stockexchange_data
  try:
    random.seed( sum([ord(i) for i in stocksymbol]) ) # compute the same seed for a stocksymbol
    for k,v in stockexchange_data.items():
      randomized_stockexchange_data[k] = {
        'lat':v['lat'], 
        'lat2':latitude, 
        'long':v['long'], 
        'long2:':longitude, 
        'available': random.randint(0,1), 
        'distance': math.sqrt(math.pow(float(v['lat'])-float(latitude),2) + math.pow(float(v['long'])-float(longitude),2)) 
      }
    #pprint(randomized_stockexchange_data)
    # compute the stock exchange that is closest and has the stock available (randomized)
    min_distance_stockexchange = None
    for k,v in randomized_stockexchange_data.items():
      if v['available'] == 1:
	if min_distance_stockexchange == None or min_distance_stockexchange['distance'] > v['distance']:
	  min_distance_stockexchange = v
	  min_distance_stockexchange['closestStockExchange'] = k
    result = { 
      'closestStockExchange':min_distance_stockexchange['closestStockExchange'],
      'closestStockExchange_lat':min_distance_stockexchange['lat'],
      'closestStockExchange_long':min_distance_stockexchange['long']
    }
  except Exception as e:
    print e
    result = {}
  print("in: %s, %s, %s   out: %s" % (stocksymbol,latitude,longitude,result) )
  return result


def init_data():
  # read data from CSV file
  reader = csv.reader(open('closeststockexchange.csv', 'r'))
  global stockexchange_data
  stockexchange_data = {}
  for row in reader:
    name,long,lat = row
    stockexchange_data[name] = { 'lat':lat, 'long':long }


def main():
  init_service(
      port=8185, 
      servicename='ClosestStockExchange',
      userfunction=closeststockexchange,
      args={'stocksymbol':str, 'latitude':str, 'longitude':str},
      returns={'closestStockExchange':str, 'closestStockExchange_lat':str, 'closestStockExchange_long':str}
  )


if  __name__ =='__main__':
  init_data()
  #closeststockexchange( 'SAP', '8.6', '50.1' )
  main()
