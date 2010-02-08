/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.googlecode.osde.internal.ui;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.osde.internal.OsdePreferencesModel;
import com.googlecode.osde.internal.utils.OpenSocialUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author chingyichan.tw@gmail.com (qrtt1)
 */
public class OsdePreferenceBinder {

    private Map<String, Object> store = new HashMap<String, Object>();
    private Map<String, Class<?>> types = new HashMap<String, Class<?>>();
    private DataBindingContext context = new DataBindingContext();
    private OsdePreferencesModel model;

    private static final Class<?>[] supportedTypes = new Class<?>[]{
        Boolean.class, String.class
    };

    public OsdePreferenceBinder(OsdePreferencesModel model) {
        this.model = model;
    }

    /**
     * @param type only support String or Boolean
     */
    public void bind(Control control, String preferenceName, Class<?> type) {
        bind(control, preferenceName, type, null, null);
    }

    /**
     * @param control supports Button (Radio or Checkbox) and Text.
     * @param type supports String and Boolean
     */
    public void bind(Control control, String preferenceName, Class<?> type,
            IConverter targetToModel, IConverter modelToTarget) {

        if (!ArrayUtils.contains(supportedTypes, type)) {
            throw new IllegalArgumentException("type[" + type + "] is not supported.");
        }

        IObservableValue model = new ObservableMapValue(store, preferenceName);
        IObservableValue ui = null;

        UpdateValueStrategy modelUpdater = new UpdateValueStrategy();
        UpdateValueStrategy uiUpdater = new UpdateValueStrategy();

        if (targetToModel != null) {
            modelUpdater.setConverter(targetToModel);
        }

        if (modelToTarget != null) {
            uiUpdater.setConverter(modelToTarget);
        }

        if (control instanceof Button) {
            boolean isRadio = (control.getStyle() & SWT.RADIO) != 0;
            boolean isCheck = (control.getStyle() & SWT.CHECK) != 0;
            if (isRadio || isCheck) {
                ui = SWTObservables.observeSelection(control);
            }
        }
        
        if (control instanceof Text) {
            ui = SWTObservables.observeText(control, SWT.Modify);
        }

        if (ui == null) {
            throw new UnsupportedOperationException(control + " is not supported yet.");
        }

        propagateData(preferenceName, type);
        context.bindValue(ui, model, modelUpdater, uiUpdater);
    }

    private void propagateData(String preferenceName, Class<?> type) {
        types.put(preferenceName, type);
        if (String.class.equals(type)) {
            store.put(preferenceName, model.get(preferenceName));
        } else {
            store.put(preferenceName, model.getBoolean(preferenceName));
        }
    }

    public void updateUI() {
        context.updateTargets();
    }

    public void store() {
        model.store(store);
    }

    public void updateDefaultUI() {
        for (String preferenceName : store.keySet()) {
            if (String.class.equals(types.get(preferenceName))) {
                store.put(preferenceName, model.getDefault(preferenceName));
            } else {
                store.put(preferenceName, model
                        .getDefaultBoolean(preferenceName));
            }
        }
        updateUI();
    }

    static class ObservableMapValue extends AbstractObservableValue {

        private String key;
        private Map<String, Object> data;

        public ObservableMapValue(Map<String, Object> data, String key) {
            this.data = data;
            this.key = key;

        }

        @Override
        protected Object doGetValue() {
            return data.get(key);
        }

        public Object getValueType() {
            return null;
        }

        @Override
        protected void doSetValue(Object value) {
            data.put(key, value);
        }
    }

    static abstract class ConverterAdapter implements IConverter {

        public abstract Object convert(Object fromObject);

        public Object getFromType() {
            return null;
        }

        public Object getToType() {
            return null;
        }
    }

    static class LocaleConverter extends ConverterAdapter {

        private String[] list;
        public LocaleConverter(String[] list) {
            this.list = list;
        }

        private boolean getValue(String data, String lang) {
            return StringUtils.equals(data, StringUtils.substringBetween(lang, "(", ")"));
        }

        @Override
        public Object convert(Object fromObject) {
            if (fromObject instanceof String) {
                String data = (String) fromObject;
                String value = StringUtils.substringBetween(data, "(", ")");

                if (value != null) {
                    return value;
                }

                String result = null;
                for (String item : list) {
                    if (getValue(data, item)) {
                        result = item;
                        break;
                    }
                }
                if (result != null) {
                    return result;
                }
            }
            return null;
        }
    }

    final static ConverterAdapter LANGUAGE_CONVERTER = new LocaleConverter(OpenSocialUtil.LANGUAGES);
    final static ConverterAdapter COUNTRY_CONVERTER = new LocaleConverter(OpenSocialUtil.COUNTRIES);

}
