package com.example.routing

class InvalidParameterException(s: String?) : IllegalArgumentException(s)

class EmptyResultException(s: String?) : NullPointerException(s)