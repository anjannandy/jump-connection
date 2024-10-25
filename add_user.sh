#!/bin/sh
adduser -D -s /bin/sh -u 1000 telnetuser
echo "telnetuser:password" | chpasswd