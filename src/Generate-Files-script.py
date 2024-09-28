import os
import random
import string

def generate_random_words(num_words):
    words = []
    for _ in range(num_words):
        word = ''.join(random.choices(string.ascii_lowercase, k=random.randint(3, 10)))
        words.append(word)
    return words

def write_file(file_path, words):
    with open(file_path, 'w') as f:
        f.write(' '.join(words))

def generate_test_files(main_file_path, pool_dir, num_pool_files, main_file_words, pool_file_words_range):
    os.makedirs(pool_dir, exist_ok=True)
    
    # Generate main file A
    main_words = generate_random_words(main_file_words)
    write_file(main_file_path, main_words)
    
    # Generate pool of files
    for i in range(num_pool_files):
        pool_file_path = os.path.join(pool_dir, f'file_{i+1}.txt')
        num_words = random.randint(*pool_file_words_range)
        pool_words = random.sample(main_words, min(len(main_words), num_words))
        pool_words += generate_random_words(num_words - len(pool_words))
        random.shuffle(pool_words)
        write_file(pool_file_path, pool_words)

# Parameters
main_file_path = 'file_A.txt'
pool_dir = 'pool_files'
num_pool_files = 10
main_file_words = 1000
pool_file_words_range = (500, 1500)

generate_test_files(main_file_path, pool_dir, num_pool_files, main_file_words, pool_file_words_range)