from classroom.models import *
from routine.models import *
import json


def getUserName(user):
    if user.first_name != "":
        return user.first_name + " " + user.last_name
    else:
        return user.username


def isValidUser(user):
    return user and user.is_authenticated and user.is_active


def getTeachers(user):
    return Teacher.objects.filter(user=user)


def getStudents(user):
    return Student.objects.filter(user=user)


def get_profiles(user):
    profile = UserProfile.objects.get(user=user)
    result = [profile.profile]

    teachers = getTeachers(user)
    for teacher in teachers:
        if teacher.department:
            result = result + [teacher.department.profile]

    students = getStudents(user)
    for student in students:
        cls = student.group.p_class
        result = result + [cls.profile]
        if cls.department:
            result = result + [cls.department.profile]

    return [r.pk for r in result]


def get_classes(user):
    result = []

    teachers = getTeachers(user)
    for teacher in teachers:
        periods = Period.objects.filter(teachers__pk=teacher.pk)
        for p in periods:
            result.append(p.routine.p_class)

    students = getStudents(user)
    for student in students:
        result.append(student.group.p_class)

    # admins as well
    admin_classes = Class.objects.filter(admins__pk=user.pk)
    for c in admin_classes:
        result.append(c)

    return list(set([r.pk for r in result]))


def get_departments(user):
    result = []

    teachers = getTeachers(user)
    for teacher in teachers:
        if teacher.department:
            result.append(teacher.department)

        periods = Period.objects.filter(teachers__pk=teacher.pk)
        for p in periods:
            if p.routine.p_class.department:
                result.append(p.routine.p_class.department)

    students = getStudents(user)
    for student in students:
        if student.group.p_class.department:
            result.append(student.group.p_class.department)

    admin_ds = Department.objects.filter(admins__pk=user.pk)
    for d in admin_ds:
        result(append(d))

    return list(set([r.pk for r in result]))
