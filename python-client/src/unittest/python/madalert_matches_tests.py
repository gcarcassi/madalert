#from mockito import mock, verify
import unittest
import sys

from madalert import *

def loadJSON(path):
    return retrievegrid(path)

class HelloWorldTest(unittest.TestCase):
    def test_matchAllSites(self):
        data = loadJSON("src/unittest/resources/allMissing.json")
        result = match_all_sites(data, 3)
        self.assertEquals(result, True)

        data = loadJSON("src/unittest/resources/allWell.json")
        result = match_all_sites(data, 3)
        self.assertEquals(result, False)

    def test_matchSite(self):
        data = loadJSON("src/unittest/resources/site3Down.json")
        result = match_site(data, 3, 3)
        self.assertEquals(result, True)
        result = match_site(data, 2, 3)
        self.assertEquals(result, False)

        data = loadJSON("src/unittest/resources/site2CantTest.json")
        result = match_site(data, 2, 3)
        self.assertEquals(result, False)
        result = match_site(data, 3, 3)
        self.assertEquals(result, False)

    def test_matchInitiatedBySites(self):
        data = loadJSON("src/unittest/resources/site3Down.json")
        result = match_initiated_by_site(data, 3, 3)
        self.assertEquals(result, True)
        result = match_initiated_by_site(data, 2, 3)
        self.assertEquals(result, False)

        data = loadJSON("src/unittest/resources/site2CantTest.json")
        result = match_initiated_by_site(data, 2, 3)
        self.assertEquals(result, True)
        result = match_initiated_by_site(data, 2, 3, 1.0)
        self.assertEquals(result, True)
        result = match_initiated_by_site(data, 2, 3, 0.75)
        self.assertEquals(result, True)
        result = match_initiated_by_site(data, 3, 3)
        self.assertEquals(result, False)

        data = loadJSON("src/unittest/resources/site2MostlyCantTest.json")
        result = match_initiated_by_site(data, 2, 3)
        self.assertEquals(result, False)
        result = match_initiated_by_site(data, 2, 3, 1.0)
        self.assertEquals(result, False)
        result = match_initiated_by_site(data, 2, 3, 0.75)
        self.assertEquals(result, True)
        result = match_initiated_by_site(data, 3, 3)
        self.assertEquals(result, False)

        data = loadJSON("src/unittest/resources/site4CantBeTested.json")
        result = match_initiated_by_site(data, 4, 3)
        self.assertEquals(result, False)
        result = match_initiated_by_site(data, 3, 3)
        self.assertEquals(result, False)

    def test_matchInitiatedOnSites(self):
        data = loadJSON("src/unittest/resources/site3Down.json")
        result = match_initiated_on_site(data, 3, 3)
        self.assertEquals(result, True)
        result = match_initiated_on_site(data, 2, 3)
        self.assertEquals(result, False)

        data = loadJSON("src/unittest/resources/site2CantTest.json")
        result = match_initiated_on_site(data, 2, 3)
        self.assertEquals(result, False)
        result = match_initiated_on_site(data, 3, 3)
        self.assertEquals(result, False)

        data = loadJSON("src/unittest/resources/site4CantBeTested.json")
        result = match_initiated_on_site(data, 4, 3)
        self.assertEquals(result, True)
        result = match_initiated_on_site(data, 3, 3)
        self.assertEquals(result, False)

        data = loadJSON("src/unittest/resources/site4MostlyCantBeTested.json")
        result = match_initiated_on_site(data, 4, 3)
        self.assertEquals(result, False)
        result = match_initiated_on_site(data, 4, 3, 1.0)
        self.assertEquals(result, False)
        result = match_initiated_on_site(data, 4, 3, 0.75)
        self.assertEquals(result, True)
        result = match_initiated_on_site(data, 3, 3)
        self.assertEquals(result, False)


