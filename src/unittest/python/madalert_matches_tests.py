#from mockito import mock, verify
import unittest
import sys

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
        result = matchInitiatedBySite(data, 2, 3, 1.0)
        self.assertEquals(result, True)
        result = matchInitiatedBySite(data, 2, 3, 0.75)
        self.assertEquals(result, True)
        result = matchInitiatedBySite(data, 3, 3)
        self.assertEquals(result, False)

        data = loadJSON("src/unittest/resources/site2MostlyCantTest.json")
        result = matchInitiatedBySite(data, 2, 3)
        self.assertEquals(result, False)
        result = matchInitiatedBySite(data, 2, 3, 1.0)
        self.assertEquals(result, False)
        result = matchInitiatedBySite(data, 2, 3, 0.75)
        self.assertEquals(result, True)
        result = matchInitiatedBySite(data, 3, 3)
        self.assertEquals(result, False)

        data = loadJSON("src/unittest/resources/site4CantBeTested.json")
        result = matchInitiatedBySite(data, 4, 3)
        self.assertEquals(result, False)
        result = matchInitiatedBySite(data, 3, 3)
        self.assertEquals(result, False)

    def test_matchInitiatedOnSites(self):
        data = loadJSON("src/unittest/resources/site3Down.json")
        result = matchInitiatedOnSite(data, 3, 3)
        self.assertEquals(result, True)
        result = matchInitiatedOnSite(data, 2, 3)
        self.assertEquals(result, False)

        data = loadJSON("src/unittest/resources/site2CantTest.json")
        result = matchInitiatedOnSite(data, 2, 3)
        self.assertEquals(result, False)
        result = matchInitiatedOnSite(data, 3, 3)
        self.assertEquals(result, False)

        data = loadJSON("src/unittest/resources/site4CantBeTested.json")
        result = matchInitiatedOnSite(data, 4, 3)
        self.assertEquals(result, True)
        result = matchInitiatedOnSite(data, 3, 3)
        self.assertEquals(result, False)

        data = loadJSON("src/unittest/resources/site4MostlyCantBeTested.json")
        result = matchInitiatedOnSite(data, 4, 3)
        self.assertEquals(result, False)
        result = matchInitiatedOnSite(data, 4, 3, 1.0)
        self.assertEquals(result, False)
        result = matchInitiatedOnSite(data, 4, 3, 0.75)
        self.assertEquals(result, True)
        result = matchInitiatedOnSite(data, 3, 3)
        self.assertEquals(result, False)


