# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2016-01-04 03:59
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('routine', '0008_period_period_type'),
    ]

    operations = [
        migrations.AlterField(
            model_name='period',
            name='teachers',
            field=models.ManyToManyField(blank=True, to='classroom.Teacher'),
        ),
    ]
