#include <stdio.h>

#include <fstream>
#include <iostream>

using namespace std;

int main() {
  cout << "{\"variants\": {";
  string s;
  while (cin >> s) {
    std::cout
        << "\"axis=x,color_portal_base_color=" + s +
               "\": {\"model\": \"color_portals:block/color_portal_block_ns_"+s+"\"  "
               "  },\"axis=z,color_portal_base_color=" +
               s +
               "\": {\"model\": \"color_portals:block/color_portal_block_ew_"+s+"\"},";
  }

  cout << "}}";
}
