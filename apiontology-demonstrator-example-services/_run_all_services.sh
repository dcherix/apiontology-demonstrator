python companydata.py -name "ontology-driven-mashup-testservice1" &
sleep 1

python citylatlong.py -name "ontology-driven-mashup-testservice2"  &
sleep 1

python weather.py  -name "ontology-driven-mashup-testservice3" &
sleep 1

python stock.py  -name "ontology-driven-mashup-testservice4" &
sleep 1

python closeststockexchange.py  "ontology-driven-mashup-testservice5"  &
sleep 1



echo ""
echo "hit enter to kill all started servers (Linux only)"
echo ""
echo ""
read 
kill `ps -ef  | grep 'ontology-driven-mashup-testservice' | grep -v grep | cut -b8-15`
ps -ef  | grep 'ontology-driven-mashup-testservice' | grep -v grep | cut -b8-15