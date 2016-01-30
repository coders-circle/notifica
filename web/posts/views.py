from django.shortcuts import render, redirect
from django.views.generic import View, TemplateView
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User

from classroom.models import *
from classroom.utils import *


class FeedView(View):
    def get(self, request):
        if isValidUser(request.user):
            context = {}
            context["current_page"] = "News feed"
            request.user.profile = UserProfile.objects.get(user=request.user).profile
            return render(request, "posts/feed.html", context)
        return redirect("home")
