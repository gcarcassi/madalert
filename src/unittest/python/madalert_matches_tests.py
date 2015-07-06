#from mockito import mock, verify
import unittest

from madalert import *

class HelloWorldTest(unittest.TestCase):
    def test_matchSiteAllStatus(self):
        result = matchSiteAllStatus(1,2,3)
        self.assertEquals(result, False)
