#pragma once

#include <stdio.h>
#include <stdbool.h>
#include <string.h>

#define BEGIN_TESTING int main(int argc, char ** argv) {
#define TEST(NAME) if (run_test(NAME, argc, argv))
#define END_TESTING return 0; }

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

BEGIN_TESTING

TEST("PassingTest") {
	assert(1 == 1);
}

TEST("FailingTest") {
	assert(1 == 2);
}

END_TESTING
*/
