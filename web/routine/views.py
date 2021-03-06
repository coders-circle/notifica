from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.db.models import Q

import json

from classroom.models import *
from classroom.utils import *
from routine.models import *
from routine.utils import *


days = ["Sunday", "Monday", "Tuesday",
        "Wednesday", "Thursday", "Friday", "Saturday"]
days_short = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]


def getGroups(groups):
    gs = []
    for g in groups:
        gs.append(Group.objects.get(pk=g["id"]))
    return gs


class RoutineView(View):
    def get(self, request):
        if not isValidUser(request.user):
            return redirect("home")
        r = getRoutine(request.user)
        context = {"days_short": days_short, "days": days, "routine": r}
        context["current_page"] = "Routine"
        context["edit_routine"] = "false"
        request.user.profile = UserProfile.objects.get(user=request.user).profile

        students = getStudents(request.user)
        student = None if len(students)==0 else students[0]
        context["student"] = student

        return render(request, 'routine/routine.html', context)


class RoutineAdminView(View):
    def get(self, request):
        if not isValidUser(request.user):
            return redirect("home")
        students = getStudents(request.user)
        student = None if len(students)==0 else students[0]
        if not student:
            redirect("routine:routine")
        if student.user not in student.group.p_class.admins.all():
            redirect("routine:routine")
        r = getRoutineForAdmin(request.user)
        context = {"days_short": days_short, "days": days, "routine": r}
        groups = Group.objects.filter(p_class__pk=student.group.p_class.pk)
        context["groups"] = groups
        context["student"] = student
        context["edit_routine"] = "true"
        request.user.profile = UserProfile.objects.get(user=request.user).profile
        return render(request, 'routine/routine-admin.html', context)

    def post(self, request):
        if not isValidUser(request.user):
            return redirect("home")

        self.tempSids = {}
        self.tempTids = {}

        # TODO: Support multiple students
        students = getStudents(request.user)
        student = None if len(students)==0 else students[0]
        if student:
            Period.objects.all().delete()
            routine = json.loads(request.POST.get('routine'))
            r = Routine.objects.get(p_class__pk=student.group.p_class.pk)

            for d, day in enumerate(routine):
                for period in day:
                    p = Period()
                    p.routine = r
                    p.period_type = period["period_type"]
                    p.subject = self.getSubject(period["subject"])
                    p.start_time = period["start_time"]
                    p.end_time = period["end_time"]
                    p.day = d
                    p.remarks = period["remarks"]
                    p.save()
                    p.teachers.add(*self.getTeachers(period["teachers"]))

                    if "groups" in period:
                        p.groups.add(*getGroups(period["groups"]))
            r.save()

        return self.get(request)


    def getSubject(self, subject):
        sid = int(subject["id"])
        if sid < 0:
            if sid in self.tempSids:
                return Subject.objects.get(pk=self.tempSids[sid])
            newSubject = Subject()
            newSubject.name = subject["name"]
            newSubject.save()
            self.tempSids[sid] = newSubject.pk
            return newSubject
        return Subject.objects.get(pk=sid)


    def getTeachers(self, teachers):
        ts = []
        for t in teachers:
            tid = int(t["id"])
            if tid < 0:
                if tid in self.tempTids:
                    ts.append(Teacher.objects.get(pk=self.tempTids[tid]))
                else:
                    teacher = Teacher()
                    teacher.username = t["name"]
                    teacher.save()
                    self.tempTids[tid] = teacher.pk
                    ts.append(teacher)
            else:
                ts.append(Teacher.objects.get(pk=tid))
        return ts


    def getStudents(self, students):
        ss = []
        for s in students:
            sid = int(s["id"])
            ss.append(Student.objects.get(sid))
        return ss
