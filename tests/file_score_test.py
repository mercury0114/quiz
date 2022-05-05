from sys import path
path.append('../')

from os import mkdir
from os.path import exists
from random import randint
from shutil import rmtree
from utils import lowest_score_file, write_open

STATISTICS_FOLDER = "/tmp/statistics/"


def generate_random_statistics(entries_count):
    statistics = {}
    for entry in range(entries_count):
        statistics[(entry, entry + 0.5)] = randint(0, 10), randint(0, 10)
    return statistics


def increment_statistics(statistics):
    for key in statistics:
        statistics[key] = statistics[key][0] + 1, statistics[key][1]


def test_random_folder():
    assert not exists(STATISTICS_FOLDER)
    for entries_count in range(1, 10):
        mkdir(STATISTICS_FOLDER)
        statistics = generate_random_statistics(entries_count)
        write_statistics(STATISTICS_FOLDER + 'file1.txt', statistics)
        increment_statistics(statistics)
        write_statistics(STATISTICS_FOLDER + 'file2.txt', statistics)
        increment_statistics(statistics)
        write_statistics(STATISTICS_FOLDER + 'file3.txt', statistics)
        assert lowest_score_file(STATISTICS_FOLDER) == 'file1.txt'
        rmtree(STATISTICS_FOLDER)


def write_statistics(file_name, statistics):
    file = write_open(file_name)
    for pair in statistics:
        file.write("{}, {}, {}, {}\n".format(pair[0],
                                             pair[1],
                                             statistics[pair][0],
                                             statistics[pair][1]))


assert lowest_score_file("file_score_data1") == "food_lower.txt"
assert lowest_score_file("file_score_data2") == "food_lower.txt"
assert lowest_score_file("file_score_data3") == "food_lower.txt"
test_random_folder()

print("file_score-test.py passed")
