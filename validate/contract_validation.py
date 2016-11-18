#!/usr/bin/python

import re
import urllib
import requests
import sys
import json

if (len(sys.argv) < 2):
    print 'Usage: %s <base url>' % sys.argv[0]
    sys.exit(1)


# validate base url?  nah
base_url = sys.argv[1]


def _callUrl(req, path, expected_status=200, **kwargs):
  headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  }

  print ("**** %s %s%s ****" % (req.upper(), base_url, path))
  full_url = '%s%s' % (base_url, path)
  r = getattr(sys.modules['requests'], req)(full_url, headers=headers, **kwargs)

  print ('RESPONSE(%d): %s' % (r.status_code, r.text))

  assert(r.status_code == expected_status)

  return r

r = _callUrl('get', '/inventory' )
inv_array = r.json()
for inv in inv_array:
    # verify by ID
    print ('=== INVENTORY : %s' % inv)

    r = _callUrl('get', '/inventory/%d' % inv['id'])

    # compare the jsons -- only this item should have been returned
    assert (cmp(r.json(), inv) == 0)


for inv in inv_array:
    print ('=== INVENTORY : %s' % inv)

    # verify by name: all inventory returned should have this name
    r = _callUrl('get', '/inventory/name/%s' % (urllib.quote(inv['name'])))
    inv_name_array = r.json()

    for inv_name in inv_name_array:
        # must have "name" property
        assert('name' in inv_name)

        # "name" must match what we queried for
        assert(inv_name['name'].find(inv['name']) >= 0)

# look for inventory with no matching name
r = _callUrl('get', '/inventory/name/%s' % (urllib.quote('DOESNT EXIST')))
inv_name_array = r.json()

assert(len(inv_name_array) == 0)

# verify by price
for inv in inv_array:
    r = _callUrl('get', '/inventory/price/%d' % (inv['price']))

    inv_price_array = r.json()

    for inv_price in inv_price_array:
        # must have "name" property
        assert('price' in inv_price)

        # "price" must be less than or equal to queried price
        assert(inv_price['price'] <= inv['price'])

# look for negative prices -- empty list 
r = _callUrl('get', '/inventory/price/0')
inv_price_array = r.json()

assert(len(inv_price_array) == 0)

# Create a new Item
new_inv = { 
      "name": "new item",
      "description": "new item description",
      "img": "/images/image.jpg",
      "imgAlt": "alt image text",
      "price": 100000
}
new_inv_json = json.dumps(new_inv)

r = _callUrl('post', "/inventory",  expected_status=201, data=new_inv_json)
# parse out my new ID (it looks like "Item succesfully added to inventory! (id = XXXXX)")
new_id_idx = r.headers['location'].rfind('/') 
new_id = r.headers['location'][new_id_idx+1:]

print 'new id is: %s' % new_id
new_inv['id'] = int(new_id)

r = _callUrl('get', "/inventory/%s" % new_id)

# compare the jsons -- only this item should have been returned
assert (cmp(r.json(), new_inv) == 0)

# update 
new_inv['name'] = 'my updated name'
new_inv_json = json.dumps(new_inv)
r = _callUrl('put', "/inventory/%s" % new_id, data=new_inv_json)

r = _callUrl('get', "/inventory/%s" % new_id)

# compare the jsons -- only this item should have been returned
assert (cmp(r.json(), new_inv) == 0)

# delete
r = _callUrl('delete', "/inventory/%s" % new_id)

r = _callUrl('get', "/inventory/%s" % new_id, expected_status=404)
