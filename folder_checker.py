from sys import argv
from os import listdir
from os.path import join, basename
from random import choice
from math import ceil, sqrt
from utils import CloseTo, SelectQuestionAnswer, ReadOpen, ReadPairsFromFile, WriteOpen
from utils import HINT, QUIT

STATISTICS_FILE = "my.stats"
INITIAL_SCORE = 9

# Each triple is (language1_phrase, language2_phrase, file_name)
def ReadTriplesFromFolder(folder):
	triples = []
	for f in listdir(folder):
		if f == STATISTICS_FILE: continue
		pairs = ReadPairsFromFile(join(folder, f))
		triples += [(pair[0], pair[1], f) for pair in pairs]
	return triples

def GetStatistics(triples, folder):
	statistics = {triple : INITIAL_SCORE for triple in triples}
	if STATISTICS_FILE not in listdir(folder): return statistics
	for line in ReadOpen(join(folder,STATISTICS_FILE)):
		word1, word2, file_name, score = line.split(', ')
		if (word1, word2, file_name) in statistics:
			statistics[(word1, word2, file_name)] = int(score)
	return statistics

def WriteStatistics(statistics, folder):
	output = WriteOpen(join(folder, STATISTICS_FILE))
	sorted_triples = sorted(statistics, key=statistics.get)
	for t in sorted_triples:
		output.write("{}, {}, {}, {}\n".format(t[0], t[1], t[2], statistics[t]))
    
def IncreaseScore(score, triple, penalty):
	penalty[triple] = max(1, penalty[triple]-1)
	return score + ceil(choice(range(score+1)) / penalty[triple])

def DecreaseScore(score, triple, penalty):
	penalty[triple] += 2
	return max(1, score - choice(range(ceil(sqrt(score)))))

if len(argv) != 2:
	print("usage:")
	print("python3 folder_checker.py [folder]")
	exit()

folder = argv[1]
triples = ReadTriplesFromFolder(folder)
statistics = GetStatistics(triples, folder)
penalty = {triple : 1 for triple in triples}

print("Press {} for hint, {} to quit".format(HINT, QUIT))
while True:
	min_score = min(statistics.values())
	triple = choice([t for t in statistics if statistics[t] <= min_score + 2])
	question, answer = SelectQuestionAnswer(triple, 2)
	score = statistics[triple]
	print("{}: {}?".format(triple[2], question)) # triple[2] is the file_name
	user_input = input()
	while user_input not in [answer, HINT, QUIT]:
		if CloseTo(user_input, answer):
			print("Close, try again")
		else:
			print("Wrong answer, try again")
			score = DecreaseScore(score, triple, penalty)
		user_input = input()
	if user_input == answer:
		score = IncreaseScore(score, triple, penalty)
	if user_input == HINT:
		print(answer)
		score = DecreaseScore(score, triple, penalty)
	if user_input == QUIT:
		print("Updating statistics and exiting")
		WriteStatistics(statistics, folder)
		exit()
	print("Changing score from {} to {}\n".format(statistics[triple], score))
	statistics[triple] = score
