#from mockito import mock, verify
import unittest

from madalert import *

def loadJSON(path):
    return retrievegrid(path)

class HelloWorldTest(unittest.TestCase):
    def test_matchAllSites(self):
        data = loadJSON("src/unittest/resources/allMissing.json")
        result = matchAllSites(data, 3)
        self.assertEquals(result, True)

        data = loadJSON("src/unittest/resources/allWell.json")
        result = matchAllSites(data, 3)
        self.assertEquals(result, False)

    def test_matchSites(self):
        data = loadJSON("src/unittest/resources/site3Down.json")
        result = matchSite(data, 3, 3)
        self.assertEquals(result, True)
        result = matchSite(data, 2, 3)
        self.assertEquals(result, False)


