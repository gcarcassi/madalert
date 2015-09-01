# madalert

Madalert creates a problem report from the data provided by [maddash](https://github.com/esnet/maddash)

# build

Madalert targets python 2.6 (though later versions should work) and uses [pybuilder](http://pybuilder.github.io/) to manage the build. Once those are setup, you can build using:

    # pyb
    # ./build-rpm.sh

You will find the prepared rpms in ./target/rpm

# install/use

Once you install the rpm, you have a few scripts available:
* maddash-check-mk.py [grid-url] - Generates a check_mk report compatible with the [local agent spec](https://mathias-kettner.de/checkmk_localchecks.html). To use it in check_mk, create a script in the local agent directory (default for Linux is /usr/lib/check_mk_agent/local). For example:

    # cat /user/lib/check_mk_agent/local
    #!/bin/bash
    maddash-check-mk.py http://maddash.aglt2.org/maddash/grids/Latency+tests+between+all+WLCG+hosts+-+Latency+Tests+Between+WLCG+Latency+Hosts

* maddash-cgi-bin-report.py [grid-url] - cgi-bin script that creates an HTML report. To use it, simply create a symbolic link in the cgi-bin directory. For example, using apache on Linux (with default cgi-bin location):

    # ln -s madalert-cgi-bin-report.py /var/www/cgi-bin/madalert-report
