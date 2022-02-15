from sys import argv
from os import listdir
from os.path import join, basename
from random import choice
from math import ceil, sqrt
from utils import CloseTo, ReadOpen, ReadDataFromFile, ReadPairsFromFile, WriteOpen
from utils import GetFileScore
from utils import HINT, QUIT, INITIAL_SCORE, MAX_SCORE, NEXT_QUESTION_INDEX

def GetStatistics(folder, group):
    pairs_scores = ReadDataFromFile(join(folder, group))
    return {ps[0] : ps[1] for ps in pairs_scores}

def WriteStatistics(folder, group, statistics):
    output = WriteOpen(join(folder, group))
    for pair in sorted(statistics, key=statistics.get):
        output.write("{}, {}, {}, {}\n".format(pair[0], pair[1], statistics[pair][0], statistics[pair][1]))

def ChooseWeakestGroup(folder):
    weakness = [(GetFileScore(join(folder, g)), g) for g in listdir(folder)]
    score_group = min(weakness)
    print("Weakest group {} with score {}\n".format(score_group[1], score_group[0]))
    return score_group[1]

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
        group = ChooseWeakestGroup(folder)
        statistics = ReadDataFromFile(join(folder, group))
        counter = len(statistics)
    if index == NEXT_QUESTION_INDEX:
        min_score = min([min(p) for p in statistics.values()])
        pair = choice([p for p in statistics if min(statistics[p]) <= min_score + 2])
        index = statistics[pair][0] >= statistics[pair][1]
        question, answer = pair[index], pair[not index]
    score = statistics[pair][index]
    print("{} more from {}".format(counter, group))
    print("Current score is", score)
    print(question)
    user_input = input()
    while user_input not in [answer, HINT, QUIT]:
        if CloseTo(user_input, answer):
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
        exit()
    print("New score is {}\n".format(score))
    statistics[pair][index] = score
    if user_input == answer: index = NEXT_QUESTION_INDEX
