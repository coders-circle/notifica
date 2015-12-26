from rest_framework import viewsets
from rest_framework import permissions

from classroom.models import *
from classroom.serializers import *
from classroom.permissions import *

class OrganizationViewSet(viewsets.ModelViewSet):
    queryset = Organization.objects.all()
    serializer_class = OrganizationSerializer
    perimission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def perform_create(self, serializer):
        serializer.save(admins=[self.request.user])


class DepartmentViewSet(viewsets.ModelViewSet):
    queryset = Department.objects.all()
    serializer_class = DepartmentSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def perform_create(self, serializer):
        organization = Organization.objects.get(id=serializer.data["organization"])
        if self.request.user not in organization.admins.all():
            raise serializers.ValidationError("You have not permission to add department to this organization")
        serializer.save()


class TeacherViewSet(viewsets.ModelViewSet):
    queryset = Teacher.objects.all()
    serializer_class = TeacherSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def perform_create(self, serializer):
        department = Department.objects.get(id=serializer.data["department"])
        if self.request.user not in department.organization.admins.all():
            raise serializers.ValidationError("You have not permission to add teacher to this department")


class SubjectViewSet(viewsets.ModelViewSet):
    queryset = Subject.objects.all()
    serializer_class = SubjectSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def perform_create(self, serializer):
        department = Department.objects.get(id=serializer.data["department"])
        if self.request.user not in department.organization.admins.all():
            raise serializers.ValidationError("You have not permission to add subject to this department")


class ClassViewSet(viewsets.ModelViewSet):
    queryset = Class.objects.all()
    serializer_class = ClassSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def perform_create(self, serializer):
        serializer.save(admins=[self.request.user])


class GroupViewSet(viewsets.ModelViewSet):
    queryset = Group.objects.all()
    serializer_class = GroupSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly)

    def perform_create(self, serializer):
        pclass = Class.objects.get(id=serializer.data["p_class"])
        if self.request.user not in pclass.admins.all():
            raise serializers.ValidationError("You have not permission to add group to this class")
        serializer.save()


class StudentViewSet(viewsets.ModelViewSet):
    queryset = Student.objects.all()
    serializer_class = StudentSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def perform_create(self, serializer):
        group = Group.objects.get(id=serializer.data["group"])
        if self.request.user not in group.p_class.admins.all():
            raise serializers.ValidationError("You have not permission to add group to this class")
        serializer.save()


class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer
    permission_classes = (IsAdminOrReadOnly,)
