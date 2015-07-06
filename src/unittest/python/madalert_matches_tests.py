#from mockito import mock, verify
import unittest

from madalert import *

def loadJSON(path):
    with open(path) as data_file:
        return json.load(data_file)



class HelloWorldTest(unittest.TestCase):
    def test_matchAllSites(self):
        data = loadJSON("src/unittest/resources/allMissing.json")
        self.assertEquals(14, len(data["columnNames"]))
        result = matchAllSites(data, 3)
        self.assertEquals(result, True)

        data = loadJSON("src/unittest/resources/allWell.json")
        self.assertEquals(14, len(data["columnNames"]))
        result = matchAllSites(data, 3)
        self.assertEquals(result, False)


