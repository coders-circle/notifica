# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2016-01-11 12:30
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('routine', '0011_elective'),
    ]

    operations = [
        migrations.AlterField(
            model_name='elective',
            name='students',
            field=models.ManyToManyField(blank=True, to='classroom.Student'),
        ),
    ]
