api = 2
core = 7.x

; Include Drupal core and any core patches from Build Kit
includes[] = http://drupalcode.org/project/buildkit.git/blob_plain/refs/heads/7.x-2.x:/drupal-org-core.make

; Download the My MCShop System install profile
projects[mcshop][type] = profile
projects[mcshop][download][type] = git
projects[mcshop][download][url] = git://github.com/ 
projects[mcshop][download][branch] = master
