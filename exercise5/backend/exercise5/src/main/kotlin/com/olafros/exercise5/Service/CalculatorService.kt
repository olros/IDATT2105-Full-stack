package com.olafros.exercise5.Service

import org.springframework.stereotype.Service

@Service
class CalculatorService {

    fun calculate(arr: List<Any>): Double {
        print(isValidArray(arr))
        var array: List<Any> = arr
        while (array.size >= 3) {
            val sum = calc((array[0] as String).toDouble(), array[1] as String, (array[2] as String).toDouble())
            array = listOf(sum, *(arrayOf(array.drop(3))))
        }
        return array[0] as Double
    }

    fun isValidArray(arr: List<Any>): Boolean {
        for (i in arr.indices) {
            print("Index: $i, value: ${arr[i]}")
            if (i % 2 == 0) {
                println("Check if ${arr[i]} is double")
                if ((arr[i] as String).toDouble() !is Double)
                    return false
            } else {
                println("Check if ${arr[i]} is string")
                if (arr[i] !is String)
                    return false
            }
        }
        return true
    }

    fun calc(number1: Double, sign: String, number2: Double): Double {
        return when (sign) {
            "+" -> number1 + number2;
            "-" -> number1 - number2;
            "/" -> number1 / number2;
            "*" -> number1 * number2;
            else -> 0.0;
        }
    };

}