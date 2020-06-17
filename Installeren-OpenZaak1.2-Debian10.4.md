# Installeren van OpenZaak 1.2 op Debian 10.4

# WAARSCHUWING: Deze installatie is puur voor het snel testen!
Dit betekend dus dat je hier nooit gegevens in moet opslaan of het ontsluiten.
De meest voor de handliggende punten van aandacht zijn bij deze installatie:
- SECRET_KEY moet nooit publiek zijn
- Database credentials wil je niet delen
- DEBUG wil je echt niet aan hebben staa


## Bronnen: 

- https://github.com/open-zaak/open-zaak/blob/1e5e4ac092b5b18376ce14115800e79b650cd4cc/docs/installation/linux.rst
- https://open-zaak.readthedocs.io/en/latest/development/getting_started.html#installation

## Omgeving opzetten

### Debian on vmware

Hoogst noodzakelijke informatie
- 8 GB (8192 MB) RAM
- 100 GB disk dynamic allocated
- network bridged adapter
- debian-10.4.0-amd64-netinst.iso
- hostname: ${HOSTNAME}
- username: open-zaak
- password: open-zaak

### Benaderen van de omgeving

Installeren van ssh-server en achterhalen wat het ip-nummer is

```
$ sudo apt-get install openssh-server
$ ip a
```

### Hostname bekend maken op de client

Pas het bestand C:\Windows\System32\drivers\etc\hosts aan en voeg de volgende regel toe:

```
192.168.111.165 ${HOSTNAME}.${DOMAIN}.local
```

## Database inrichten

```
$ sudo apt-get install postgresql postgis
$ sudo -u postgres createuser openzaak -P
open-zaak
open-zaak
$ sudo -u postgres createdb -O open-zaak open-zaak
$ sudo -u postgres psql open-zaak -c  "CREATE EXTENSION postgis;"
$ sudo -u postgres psql open-zaak -c  "CREATE EXTENSION pg_trgm;"
# kijken of de database het doet
$ psql -U open-zaak open-zaak
openzaak=> quit;

```

## Applicatie bestanden plaatsen	
```
$ sudo apt-get install git
$ sudo mkdir /srv/sites/
$ cd /srv/sites/
$ sudo git clone https://github.com/open-zaak/open-zaak.git	
$ sudo chown -R openzaak .
$ cd open-zaak
```
## Python omgeving met uwsgi
```
$ sudo apt-get install python3-dev python3-venv
$ sudo apt-get install libpq-dev postgresql-client libgdal20 libgeos-c1v5  libproj13 build-essential
$ python3 -m venv env
$ source env/bin/activate	
(env) # pip install wheel
(env) # pip install -r requirements/dev.txt
```

## Configuration
```
(env) # cp .env-example .env 
(env) # vi .env 
```

```
# .env config - see docs/conf.md for more information
DJANGO_SETTINGS_MODULE=openzaak.conf.dev
# generate with: https://miniwebtool.com/django-secret-key-generator/
SECRET_KEY=28%w)*)jz6xzo^&$9aockmugxk*y5g-i&svxj=7nf7*jjbr#eg
# TODO: remove next line when front-proxy is up and running
ALLOWED_HOSTS=*
#ALLOWED_HOSTS=${HOSTNAME}
LOG_STDOUT=1

DB_HOST=localhost
DB_NAME=open-zaak
DB_USER=open-zaak
DB_PASSWORD=open-zaak

CACHE_DEFAULT=redis-cache:6379/0
CACHE_AXES=redis-cache:6379/0
EMAIL_HOST=mail.gemeente.nl
```
### Controle
```
(env) # python3 src/manage.py check
System check identified no issues (1 silenced).
(env) # python3 src/manage.py migrate
(overal een groene "OK"
(env) # python3 src/manage.py collectstatic	
```
### Superuser
```
(env) # python src/manage.py createsuperuser
Gebruikersnaam: open-zaak
E-mailadres: open-zaak@nergens.org
Password: open-zaak
Password (again): open-zaak
Het wachtwoord lijkt te veel op de gebruikersnaam.
Bypass password validation and create user anyway? [y/N]: y
```
### Starten uwsgi
```
(env) # /srv/sites/open-zaak/env/bin/uwsgi --http :8000  --module openzaak.wsgi  --chdir /srv/sites/open-zaak/src  --processes 2  --threads 2  --buffer-size 32768
```
Probeer met de browser op url: http://${HOSTNAME}:8000/
**Deze laten draaien, want deze wordt gebruikt door de front-proxy**

## Genereren van de certificaten
```
$ sudo openssl dhparam -dsaparam -out /etc/ssl/certs/dhparam.pem 4096
$ sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/ssl/certs/private.key -out /etc/ssl/certs/public.cert
```
**let erop dat de "Common Name (e.g. server FQDN or YOUR name) []:" de servernaam: '${HOSTNAME}.${DOMAIN}.local' is!**

```	
$ sudo mkdir /etc/ssl/sites
$ sudo ln -s /etc/ssl/certs/public.cert /etc/ssl/sites/public.cert
$ sudo ln -s /etc/ssl/certs/private.key /etc/ssl/sites/private.key
```

## Inrichten front-proxy
```
$ sudo apt-get install nginx
$ sudo cp /srv/sites/open-zaak/docs/installation/code/nginx-vhost.conf /etc/nginx/sites-available/open-zaak
$ sudo vi /etc/nginx/sites-available/open-zaak
```

Diff (*deze moet nog wat liefde hebben*):
```
9,10c9,10
<     server_name open-zaak.gemeente.nl;
<     rewrite ^ https://open-zaak.gemeente.nl$request_uri?;
---
>     server_name open-zaak.local;
>     rewrite ^ https://${HOSTNAME}.${DOMAIN}.local$request_uri?;
17c17
<     server_name open-zaak.gemeente.nl;
---
>     server_name ${HOSTNAME}.${DOMAIN}.local;
67,68c67,68
<     access_log /srv/sites/production/log/nginx/access.log;
<     error_log /srv/sites/production/log/nginx/error.log info;
---
>     access_log /srv/sites/open-zaak/log/nginx/access.log;
>     error_log /srv/sites/open-zaak/log/nginx/error.log info;
72c72
<         alias /srv/sites/production/static/;
---
>         alias /srv/sites/open-zaak/static/;
77c77
<         alias /srv/sites/production/media/;
---
>         alias /srv/sites/open-zaak/media/;
105c105
<         root /srv/sites/production/src/openzaak/templates/;
---
>         root /srv/sites/open-zaak/src/openzaak/templates/;
```



```
$ sudo rm /etc/nginx/sites-enabled/default
$ sudo ln -s /etc/nginx/sites-available/open-zaak /etc/nginx/sites-enabled/open-zaak
$ sudo nginx -t
$ sudo /etc/init.d/nginx restart
```


### Configureren Open-Zaak zelf
Ga naar https://${HOSTNAME}.${DOMAIN}.local/admin/
Login met open-zaak / open-zaak

Ga naar https://${HOSTNAME}.${DOMAIN}.local/admin/sites/site/

En pas example.com aan naar: ${HOSTNAME}.${DOMAIN}.local

Ga naar https://${HOSTNAME}.${DOMAIN}.local/view-config/

Klik door naar "bekijk configuratie" om te kijken of overal groene vinkjes staan

Applicatie toevoegen op https://${HOSTNAME}.${DOMAIN}.local/admin/authorizations/applicatie
Bearer dan copieren naar de applicatie die deze openzaak gaat gebruiken

## Gewoon draaien nadat al het bovenstaande is gedaan
$ cd /srv/sites/open-zaak
$ source env/bin/activate
(env) # /srv/sites/open-zaak/env/bin/uwsgi --http :8000  --module openzaak.wsgi  --chdir /srv/sites/open-zaak/src  --processes 2  --threads 2  --buffer-size 32768

## Update vanuit git doorvoeren

$ cd /srv/sites/open-zaak
$ git checkout

$ # python3 src/manage.py migrate

(env) # /srv/sites/open-zaak/env/bin/uwsgi --http :8000  --module openzaak.wsgi  --chdir /srv/sites/open-zaak/src  --processes 2  --threads 2  --buffer-size 32768
