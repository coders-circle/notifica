# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2016-01-02 02:55
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0006_teacher_username'),
    ]

    operations = [
        migrations.AddField(
            model_name='subject',
            name='short_name',
            field=models.CharField(default='', max_length=5),
        ),
    ]