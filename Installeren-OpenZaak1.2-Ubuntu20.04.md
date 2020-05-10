# Installeren van OpenZaak 1.2 op Ubuntu 20.04

Als uitgangsunt is hiervoor het volgende gebruikt: 
https://github.com/open-zaak/open-zaak/blob/1e5e4ac092b5b18376ce14115800e79b650cd4cc/docs/installation/linux.rst

## Omgeving opzetten

1 - VMWare:
	8 GB ram
	100 GB disk

2 - ubuntu-20.04-desktop-amd64.iso
	hostname: openzaak

3 - login:
	username: openzaak
	password: openzaak

4 - install VirtualBox Utils

5 - ip a
	notepad++ C:\Windows\System32\drivers\etc\hosts
	192.168.111.164 open-zaak.local

6 - $ sudo apt-get install openssh-server 

## Database inrichten

7 - $sudo apt-get install postgresql postgis postgresql-12-postgis-3 postgresql-12-postgis-3-scripts

8 - $sudo apt-get install libpq-dev postgresql-client libgdal26 libgeos-c1v5  libproj15

9 - $sudo -u postgres createuser openzaak -P
	Enter password for new role:
	Enter it again:
	createdb 
	
10 - 
	$ sudo -u postgres createdb -O openzaak openzaak
	$ sudo -u postgres psql openzaak -c  "CREATE EXTENSION postgis;"
	$ sudo -u postgres psql openzaak -c  "CREATE EXTENSION pg_trgm;"
	`# kijken of de database het doet
	$ psql -U openzaak -P openzaak
	openzaak=> quit;

## Applicatie bestanden plaatsen	

11 - 
	$ sudo mkdir /srv/sites/
	$ sudo apt-get install git

12 - 
	$ sudo git clone https://github.com/open-zaak/open-zaak.git	

​	$ sudo chown -R openzaak .
​	#$ cd production
​	$ cd open-zaak

## Inrichten python webserver (Django / uwsgi)	

13 - 
	$ sudo apt-get install python3-dev python3-venv build-essentials
	$ sudo python3 -m venv env
	$ source env/bin/activate	
	
14 - 
	(env) # pip install wheel
	(env) # pip install -r requirements/dev.txt

15 - 
	(env) # cp .env-example .env 
	(env) # vi .env 
	(env) # sed -i 's|openzaak.conf.production|openzaak.conf.dev|g' .env 
	(env) # sed -i 's|gemeente.nl,open-zaak.gemeente.nl|*|g' .env 
	replace the SECRET_KEY met iet gegevenereerd
	
16 - 
	(env) # python3 src/manage.py check
	System check identified no issues (1 silenced).
	(env) # python3 src/manage.py migrate
	(overal een groene "OK"
	(env) # python3 src/manage.py collectstatic
	
17 - 
	(env) # python src/manage.py createsuperuser
	Gebruikersnaam: openzaak
	E-mailadres: openzaak@nergens.org
	Password: openzaak
	Password (again): openzaak
	Het wachtwoord lijkt te veel op de gebruikersnaam.
	Bypass password validation and create user anyway? [y/N]: y

18 - 
	(env) # /srv/sites/open-zaak/env/bin/uwsgi --http :8000  --module openzaak.wsgi  --chdir /srv/sites/open-zaak/src  --processes 2  --threads 2  --buffer-size 32768
	#uit te proberen op poort https:8000
	
19 - 
	(env) # python src/manage.py runserver

## Inrichten front-proxy

20 - 
	$ sudo apt-get install nginx
	
21 - 
	$ sudo openssl dhparam -dsaparam -out /etc/ssl/certs/dhparam.pem 4096
	$ sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/ssl/certs/private.key -out /etc/ssl/certs/public.cert
		# let erop dat de "Common Name (e.g. server FQDN or YOUR name) []:" de servernaam: 'open-zaak.local' is!
		
	$ sudo mkdir /etc/ssl/sites
	$ sudo ln -s /etc/ssl/certs/public.cert /etc/ssl/sites/public.cert

22 - 
	$ sudo ln -s /srv/sites/open-zaak/docs/installation/code/nginx-vhost.conf /etc/nginx/sites-available/openzaak
	# of moeten we dit anders toch maar op /srv/sites/open-zaak/docs/installation/code/nginx-vhost.conf doen?
​	$ sudo sed -i 's|/sites/production/|/sites/open-zaak//|g' /etc/nginx/sites-available/openzaak
​	$ sudo sed -i 's|open-zaak.gemeente.nl|openzaak|g' /etc/nginx/sites-available/openzaak
​	$ sudo rm /etc/nginx/sites-enabled/default
​	$ sudo ln -s /etc/nginx/sites-available/openzaak /etc/nginx/sites-enabled/openzaak
​	
23 - 
​	$ sudo nginx -t
​	$ sudo /etc/init.d/nginx restart

24 - 
	Ga naar https://openzaak/
	Login met openzaak / openzaak
	Klik door naar "bekijk configuratie" om te kijken of overal groene vinkjes staan
	
25 - 
	Aanpassen van example.com bij https://openzaak/admin/sites/site/ 
		naar: openzaak
		
26 - 
	Applicatie toevoegen op https://openzaak/admin/authorizations/applicatie
	Bearer dan copieren naar de applicatie die deze openzaak gaat gebruiken

## Gewoon draaien nadat al het bovenstaande is gedaan
$ cd /srv/sites/open-zaak
$ source env/bin/activate
(env) # /srv/sites/open-zaak/env/bin/uwsgi --http :8000  --module openzaak.wsgi  --chdir /srv/sites/open-zaak/src  --processes 2  --threads 2  --buffer-size 32768