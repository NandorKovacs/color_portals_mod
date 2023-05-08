#include <stdio.h>

#include <fstream>
#include <iostream>

using namespace std;

int main() {
  string s;
  while (cin >> s) {
    fstream fs;
    fs.open("color_portal_block_ew_" + s + ".json", std::ios::out);
    fs << "{\"textures\": {\"particle\": \"color_portals:block/color_portal_block/"+ s +"\",\"portal\": \"color_portals:block/color_portal_block/"+ s +"\"},\"elements\": [{   \"from\": [ 6, 0, 0 ],\"to\": [ 10, 16, 16 ],\"faces\": {\"east\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#portal\" },\"west\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#portal\" }}}]}";
    fs.close();
    fs.open("color_portal_block_ns_" + s + ".json", std::ios::out);
    fs << "{\"textures\": {\"particle\": \"color_portals:block/color_portal_block/"+ s +"\",\"portal\": \"color_portals:block/color_portal_block/"+ s +"\"},\"elements\": [{   \"from\": [ 0, 0, 6 ],\"to\": [ 16, 16, 10 ],\"faces\": {\"north\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#portal\" },\"south\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#portal\" }}}]}";
    fs.close();
  }
}
