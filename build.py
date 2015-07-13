from pybuilder.core import use_plugin, init

use_plugin("python.core")
use_plugin("python.unittest")
use_plugin("python.install_dependencies")
use_plugin("python.distutils")


name = "madalert"
default_task = "publish"
version = "0.1-SNAPSHOT"

@init
def set_properties(project):
    pass

@init
def initialize(project):
    project.build_depends_on('mockito')
