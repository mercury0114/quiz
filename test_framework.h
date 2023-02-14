#pragma once

#include <stdbool.h> // bool, false, true
#include <string.h> // strcmp

#define TEST(test_name) if (run_test(test_name, argc, argv))

bool run_test(const char* test_name, int argc, char **argv) {
	if (argc == 1) {
		return true;
	}
	for (int i = 1; i < argc; i++) {
		if (!strcmp(test_name, argv[i])) {
			return true;
		}
	}
	return false;
}

// Below is an example of how your test file could look like:
/*
#include test_framework.h

int main(int argc, char ** argv) {
    TEST("PassingTest") {
	    assert(1 == 1);
    }

    TEST("FailingTest") {
	    assert(1 == 2);
    }

    return 0;
}
*/
