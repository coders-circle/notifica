# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2016-01-02 14:07
from __future__ import unicode_literals

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('posts', '0004_auto_20160102_0839'),
    ]

    operations = [
        migrations.AlterModelOptions(
            name='post',
            options={'ordering': ('posted_on',)},
        ),
    ]