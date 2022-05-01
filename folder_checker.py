from sys import argv
from os import listdir
from os.path import join, basename
from random import choice
from math import ceil, sqrt
from utils import close_words, ReadOpen, ReadDataFromFile, ReadPairsFromFile, write_open
from utils import get_file_score, lowest_score_file
from utils import HINT, QUIT, INITIAL_SCORE, MAX_SCORE, NEXT_QUESTION_INDEX

def WriteStatistics(folder, group, statistics):
    output = write_open(join(folder, group))
    for pair in sorted(statistics, key = lambda p : statistics[p][0] + statistics[p][1]):
        output.write("{}, {}, {}, {}\n".format(pair[0], pair[1], statistics[pair][0], statistics[pair][1]))

if len(argv) != 2:
    print("usage:")
    print("python3 folder_checker.py [folder]")
    exit()

folder = argv[1]
group = None
statistics = None

print("Press {} for hint, {} to quit".format(HINT, QUIT))
counter = 0
index = NEXT_QUESTION_INDEX
while True:
    if counter == 0:
        if group: WriteStatistics(folder, group, statistics)
        group = lowest_score_file(folder)
        statistics = ReadDataFromFile(join(folder, group), read_all_words=False)
        counter = len(statistics)
        if counter == 0:
            print("No words to learn from this directory")
            quit()
    if index == NEXT_QUESTION_INDEX:
        min_score = min([min(p) for p in statistics.values()])
        pair = choice(list(statistics))
        # pair = choice([p for p in statistics if min(statistics[p]) <= (min_score + 2) * 1.3])
        index = statistics[pair][0] >= statistics[pair][1]
        question, answer = pair[index], pair[not index]
    score = statistics[pair][index]
    print("{} more from {}".format(counter, group))
    print("Current score is", score)
    print(question)
    user_input = input()
    while user_input not in [answer, HINT, QUIT]:
        if close_words(user_input, answer):
            print("Close, try again")
        else:
            print("Wrong answer, try again")
            score = max(0, score - 1)
            counter += 1
        user_input = input()
    if user_input == answer:
        score = min(MAX_SCORE, score + 1)
        counter -= 1
    if user_input == HINT:
        print(answer)
        score = max(0, score - 3)
        counter += 2
    if user_input == QUIT:
        WriteStatistics(folder, group, statistics)
        exit()
    print("New score is {}\n".format(score))
    statistics[pair][index] = score
    if user_input == answer: index = NEXT_QUESTION_INDEX
