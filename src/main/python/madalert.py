import sys
import json, urllib

def helloworld(out):
    out.write("Hello world of Python\n")

def retrievegrid(url):
    resp = urllib.urlopen(url)
    return json.load(resp)
