from sys import path
path.append('../')

from random import choice
from string import ascii_lowercase
from utils import close_words


def random_string(length):
    return ''.join(choice(ascii_lowercase) for i in range(length))


def assert_close_words(word1, word2):
    assert close_words(word1, word2), "{} and {} not close".format(word1,
                                                                   word2)


def test_random_strings():
    for i in range(25):
        word = random_string(i)
        assert_close_words(word, word)
        assert_close_words(word + 'x', word)
        assert_close_words(word, word + 'x')


assert close_words("hello", "hallo")
assert close_words("hello", "holla")
assert close_words("hello", "hell")
assert close_words("Hello", "hello")
assert close_words("hello", "ehllo")
assert not close_words("hello", "bye")
assert not close_words("hello", "h")
test_random_strings()

print("close_words-test.py passed")
