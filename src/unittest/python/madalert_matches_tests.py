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

    def test_matchSite(self):
        data = loadJSON("src/unittest/resources/site3Down.json")
        result = matchSite(data, 3, 3)
        self.assertEquals(result, True)
        result = matchSite(data, 2, 3)
        self.assertEquals(result, False)

        data = loadJSON("src/unittest/resources/site2CantTest.json")
        result = matchSite(data, 2, 3)
        self.assertEquals(result, False)
        result = matchSite(data, 3, 3)
        self.assertEquals(result, False)

    def test_matchInitiatedBySites(self):
        data = loadJSON("src/unittest/resources/site3Down.json")
        result = matchInitiatedBySite(data, 3, 3)
        self.assertEquals(result, True)
        result = matchInitiatedBySite(data, 2, 3)
        self.assertEquals(result, False)

        data = loadJSON("src/unittest/resources/site2CantTest.json")
        result = matchInitiatedBySite(data, 2, 3)
        self.assertEquals(result, True)
        result = matchInitiatedBySite(data, 3, 3)
        self.assertEquals(result, False)


