# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2016-02-13 11:43
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0018_auto_20160210_0557'),
    ]

    operations = [
        migrations.AddField(
            model_name='class',
            name='description',
            field=models.CharField(blank=True, default='', max_length=300),
        ),
    ]
