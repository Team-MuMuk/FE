package com.example.mumuk.ui.health

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MediatorLiveData

class HealthViewModel : ViewModel() {
    val gender = MutableLiveData<String?>(null)
    val height = MutableLiveData<String?>(null)
    val weight = MutableLiveData<String?>(null)

    val isStep0Complete: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        fun update() {
            val isComplete = !gender.value.isNullOrBlank() &&
                    !height.value.isNullOrBlank() &&
                    !weight.value.isNullOrBlank()
            if (value != isComplete) {
                value = isComplete
            }
        }

        addSource(gender) { update() }
        addSource(height) { update() }
        addSource(weight) { update() }
    }

    val allergies = MutableLiveData<Set<String>>(emptySet())
    val customAllergy = MutableLiveData<String?>(null)

    val isStep1Complete: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        fun update() {
            val isComplete = allergies.value?.isNotEmpty() == true || !customAllergy.value.isNullOrBlank()
            if (value != isComplete) {
                value = isComplete
            }
        }
        addSource(allergies) { update() }
        addSource(customAllergy) { update() }
    }

    val goals = MutableLiveData<Set<String>>(emptySet())
    val customGoal = MutableLiveData<String?>(null)

    val isStep2Complete: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        fun update() {
            val isComplete = goals.value?.isNotEmpty() == true || !customGoal.value.isNullOrBlank()
            if (value != isComplete) {
                value = isComplete
            }
        }
        addSource(goals) { update() }
        addSource(customGoal) { update() }
    }
}