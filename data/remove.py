#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Nov  9 22:07:13 2017

@author: kshitij
"""
file = open('stopwords.txt','r')
temp = set(file.read().splitlines())

#print(temp) 

file = open('imdb.vocab','r')
vocab = file.read().splitlines()
j = 0
stopwords = []
for i in vocab:
    if i in temp:
        stopwords.append(j)
    j = j + 1
    
stopwords = set(stopwords)

