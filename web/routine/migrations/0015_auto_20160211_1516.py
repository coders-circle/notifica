# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2016-02-11 15:16
from __future__ import unicode_literals

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('routine', '0014_auto_20160202_1430'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='elective',
            name='p_group',
        ),
        migrations.RemoveField(
            model_name='elective',
            name='students',
        ),
        migrations.RemoveField(
            model_name='elective',
            name='subject',
        ),
        migrations.RemoveField(
            model_name='elective',
            name='teachers',
        ),
        migrations.RemoveField(
            model_name='electivegroup',
            name='routine',
        ),
        migrations.DeleteModel(
            name='Elective',
        ),
        migrations.DeleteModel(
            name='ElectiveGroup',
        ),
    ]
