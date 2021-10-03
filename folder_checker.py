from sys import argv
from os import listdir
from os.path import join, basename
from random import choice
from math import ceil, sqrt
from utils import CloseTo, ReadOpen, ReadPairsFromFile, WriteOpen
from utils import GetFileScore
from utils import HINT, QUIT

STATISTICS_FILE = "my.stats"
MAX_SCORE = 30
INITIAL_SCORE = 5
GROUP_COUNT = 12
STATS_END = ".stats"
TXT_END = ".txt"

# Each triple is (language1_phrase, language2_phrase, file_name)
def ReadTriplesFromFolder(folder):
	triples = []
	for f in listdir(folder):
		if f == STATISTICS_FILE: continue
		pairs = ReadPairsFromFile(join(folder, f))
		triples += [(pair[0], pair[1], f) for pair in pairs]
		triples += [(pair[1], pair[0], f) for pair in pairs]
	return triples

def GetStatistics(folder, group):
	pairs = ReadPairsFromFile(join(folder, group + TXT_END))
	pairs += [(p[1], p[0]) for p in pairs]
	statistics = {p : INITIAL_SCORE for p in pairs}
	if group + STATS_END in listdir(folder):
		for line in ReadOpen(join(folder, group + STATS_END)):
			word1, word2, score = line.split(', ')
			score = int(score)
			if (word1, word2) in statistics:
				statistics[(word1, word2)] = score
	return statistics

def WriteStatistics(folder, group, statistics):
	if group == None: return
	output = WriteOpen(join(folder, group + STATS_END))
	for pair in sorted(statistics, key=statistics.get):
		output.write("{}, {}, {}\n".format(pair[0], pair[1], statistics[pair]))

def ChooseWeakestGroup(folder):
	groups = [f.split('.')[0] for f in listdir(folder)]
	weakness = [(GetFileScore(join(folder, g + STATS_END)), g) for g in groups]
	return min(weakness)

if len(argv) != 2:
	print("usage:")
	print("python3 folder_checker.py [folder]")
	exit()

folder = argv[1]
group = None
statistics = None

print("Press {} for hint, {} to quit".format(HINT, QUIT))
counter = 0
while True:
	if counter == 0:
		WriteStatistics(folder, group, statistics)
		group = ChooseWeakestGroup(folder)[1]
		print("NEW GROUP {}\n".format(group))
		counter = GROUP_COUNT
		statistics = GetStatistics(folder, group)
	min_score = min(statistics.values())
	pair = choice([p for p in statistics if statistics[p] <= min_score * 1.5])
	question, answer = pair[0], pair[1]
	score = statistics[pair]
	print("{} more from {}".format(counter, group))
	print("Current score is", score)
	print(question)
	user_input = input()
	while user_input not in [answer, HINT, QUIT]:
		if CloseTo(user_input, answer):
			print("Close, try again")
		else:
			print("Wrong answer, try again")
			score = max(0, score - 2)
			counter += 1
		user_input = input()
	if user_input == answer:
		score = min(MAX_SCORE, score + 1)
		counter -= 1
	if user_input == HINT:
		print(answer)
		score -= max(0, score // 2)
		counter += 2
	if user_input == QUIT:
		exit()
	print("New score is {}\n".format(score))
	statistics[pair] = score
