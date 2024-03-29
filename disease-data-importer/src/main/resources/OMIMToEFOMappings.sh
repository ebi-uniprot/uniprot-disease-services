#! /bin/bash
# This script is used to generated data for file mondo-omim2efo.txt
# we can re-run this if we want to update the omim to EFO mapping.
# Ideally we should update every release
#e.g. ./OMIMToEFOMappings.sh 50 omim2efo
## ---------------------------- UTILITY FUNCTIONS ----------------------------
function showHelp {
  cat <<EOF
Usage: $0 pageSize [omim2efo|longEfo2Omim]

Fetches omim/efo mappings from the OLS REST API (http://www.ebi.ac.uk/ols/docs/api).

Argument description:
  pageSize              => specify the number of results to be fetched in each curl request
  omim2efo              => prints omim -> efo mappings
  longEfo2Omim		=> prints efo + name -> omim mappings
  NB: if either omim2efo / longEfo2Omim is omitted, defaults to efo -> omim mappings

  Example: $0 50 -- finds the omim/efo mappings via the OLS RESTful service, using curl to fetch 50 results at a time

EOF
}

## ---------------------------- CHECK ARGUMENTS ----------------------------
if [ "$#" -ne "1" -a "$#" -ne "2" ]; then
  showHelp
  exit 1
else
  if [ "$#" -eq "2" -a "$2" == "omim2efo" ]; then
    export omim2efo="omim2efo"
    echo "# OMIM | EFO"
  elif [ "$#" -eq "2" -a "$2" == "longEfo2Omim" ]; then
    export omim2efo="longEfo2Omim"
    echo "# EFO & OMIM mappings"
  else
    export omim2efo="efo2omim"
    echo "# EFO | OMIM"
  fi
fi

size="$1"
headers="Accept: application/json"

## ---------------------------- FETCH TOTAL NUMBER OF PAGES OF RESULTS ----------------------------
url="https://www.ebi.ac.uk/ols4/api/ontologies/efo/terms?size=$size"
totalPages=""
response=$(curl --silent "$url" -H "$headers")
if [ $? -eq 0 ]; then
  totalPages=$(echo "$response" | python -c '
import sys, json
print ("{}".format(json.load(sys.stdin)["page"]["totalPages"]))
  ')
fi

## ---------------------------- FETCH EACH PAGE OF RESULTS AND FIND THE OMIM/EFO MAPPINGS ----------------------------
for pageNum in $(seq 1 $totalPages); do
  url="https://www.ebi.ac.uk/ols4/api/ontologies/efo/terms?page=$pageNum&size=$size"

  response=$(curl --silent "$url" -H "$headers")
  if [ $? -ne 0 ]; then
    echo "Error occurred while fetching data from the API. URL: $url"
    exit 1
  fi

  echo "$response" | python -c '
import sys, json, os
from importlib import reload

omim2efo=os.environ["omim2efo"]
#sys.stdout=codecs.getwriter('utf-8')(sys.stdout)
#UTF8Writer = codecs.getwriter("utf8")
#sys.stdout = UTF8Writer(sys.stdout)

#UTF8Reader = codecs.getreader("utf8")
#sys.stdin = UTF8Reader(sys.stdin)

reload(sys)
#sys.setdefaultencoding("utf-8")

terms=json.load(sys.stdin)
try:
   for term in terms["_embedded"]["terms"]:
      if term["is_obsolete"] == False:
         efo=term["iri"]
         #print term
         if term["obo_xref"] != None:
            if omim2efo == "omim2efo":
               #print "bbb"

               for obo_xref in term["obo_xref"]:
                  #print "ccc"

                  if obo_xref["database"] == "OMIM":
                     omim = obo_xref["id"]
#                     print ("OMIM:{}\t{}\t{}".format(omim, efo, term["label"]))
                     print ("OMIM:{}\t{}".format(omim, efo))
            elif omim2efo == "longEfo2Omim":
               print ("{}\t{}\t{}".format(efo, term["label"], ",".join(term["annotation"]["OMIM_definition_citation"])))
            else:
               print ("{}\t{}".format(efo, ",".join(term["annotation"]["OMIM_definition_citation"])))
except KeyError:
    pass
'
done
