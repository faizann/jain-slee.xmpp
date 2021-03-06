/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jivesoftware.smackx;

import java.util.*;

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smackx.packet.DataForm;

/**
 * Represents a Form for gathering data. The form could be of the following types:
 * <ul>
 *  <li>form -> Indicates a form to fill out.</li>
 *  <li>submit -> The form is filled out, and this is the data that is being returned from 
 * the form.</li>
 *  <li>cancel -> The form was cancelled. Tell the asker that piece of information.</li>
 *  <li>result -> Data results being returned from a search, or some other query.</li>
 * </ul>
 * 
 * Depending of the form's type different operations are available. For example, it's only possible
 * to set answers if the form is of type "submit".
 * 
 * @author Gaston Dombiak
 */
public class Form {
    
    public static final String TYPE_FORM = "form";
    public static final String TYPE_SUBMIT = "submit";
    public static final String TYPE_CANCEL = "cancel";
    public static final String TYPE_RESULT = "result";
    
    private DataForm dataForm;
    
    /**
     * Returns a new ReportedData if the packet is used for gathering data and includes an 
     * extension that matches the elementName and namespace "x","jabber:x:data".  
     * 
     * @param packet the packet used for gathering data.
     */
    public static Form getFormFrom(Packet packet) {
        // Check if the packet includes the DataForm extension
        PacketExtension packetExtension = packet.getExtension("x","jabber:x:data");
        if (packetExtension != null) {
            // Check if the existing DataForm is not a result of a search
            DataForm dataForm = (DataForm) packetExtension;
            if (dataForm.getReportedData() == null)
                return new Form(dataForm);
        }
        // Otherwise return null
        return null;
    }

    /**
     * Creates a new Form that will wrap an existing DataForm. The wrapped DataForm must be
     * used for gathering data. 
     * 
     * @param dataForm the data form used for gathering data. 
     */
    private Form(DataForm dataForm) {
        this.dataForm = dataForm;
    }
    
    /**
     * Creates a new Form of a given type from scratch.<p>
     *  
     * Possible form types are:
     * <ul>
     *  <li>form -> Indicates a form to fill out.</li>
     *  <li>submit -> The form is filled out, and this is the data that is being returned from 
     * the form.</li>
     *  <li>cancel -> The form was cancelled. Tell the asker that piece of information.</li>
     *  <li>result -> Data results being returned from a search, or some other query.</li>
     * </ul>
     * 
     * @param type the form's type (e.g. form, submit,cancel,result).
     */
    public Form(String type) {
        this.dataForm = new DataForm(type);
    }
    
    /**
     * Adds a new field to complete as part of the form.
     * 
     * @param field the field to complete.
     */
    public void addField(FormField field) {
        dataForm.addField(field);
    }
    
    /**
     * Sets a new String value to a given form's field. The field whose variable matches the 
     * requested variable will be completed with the specified value. If no field could be found 
     * for the specified variable then an exception will be raised.<p>
     * 
     * If the value to set to the field is not a basic type (e.g. String, boolean, int, etc.) you
     * can use this message where the String value is the String representation of the object. 
     * 
     * @param variable the variable name that was completed.
     * @param value the String value that was answered.
     * @throws IllegalStateException if the form is not of type "submit".
     * @throws IllegalArgumentException if the form does not include the specified variable.
     * @throws IllegalArgumentException if the answer type does not correspond with the field type.
     */
    public void setAnswer(String variable, String value) {
        FormField field = getField(variable);
        if (field == null) {
            throw new IllegalArgumentException("Field not found for the specified variable name.");
        }
        if (!FormField.TYPE_TEXT_MULTI.equals(field.getType())
            && !FormField.TYPE_TEXT_PRIVATE.equals(field.getType())
            && !FormField.TYPE_TEXT_SINGLE.equals(field.getType())
            && !FormField.TYPE_JID_SINGLE.equals(field.getType())
            && !FormField.TYPE_HIDDEN.equals(field.getType())) {
            throw new IllegalArgumentException("This field is not of type String.");
        }
        setAnswer(field, value);
    }

    /**
     * Sets a new int value to a given form's field. The field whose variable matches the 
     * requested variable will be completed with the specified value. If no field could be found 
     * for the specified variable then an exception will be raised.
     * 
     * @param variable the variable name that was completed.
     * @param value the int value that was answered.
     * @throws IllegalStateException if the form is not of type "submit".
     * @throws IllegalArgumentException if the form does not include the specified variable.
     * @throws IllegalArgumentException if the answer type does not correspond with the field type.
     */
    public void setAnswer(String variable, int value) {
        FormField field = getField(variable);
        if (field == null) {
            throw new IllegalArgumentException("Field not found for the specified variable name.");
        }
        if (!FormField.TYPE_TEXT_MULTI.equals(field.getType())
            && !FormField.TYPE_TEXT_PRIVATE.equals(field.getType())
            && !FormField.TYPE_TEXT_SINGLE.equals(field.getType())) {
            throw new IllegalArgumentException("This field is not of type int.");
        }
        setAnswer(field, new Integer(value));
    }

    /**
     * Sets a new long value to a given form's field. The field whose variable matches the 
     * requested variable will be completed with the specified value. If no field could be found 
     * for the specified variable then an exception will be raised.
     * 
     * @param variable the variable name that was completed.
     * @param value the long value that was answered.
     * @throws IllegalStateException if the form is not of type "submit".
     * @throws IllegalArgumentException if the form does not include the specified variable.
     * @throws IllegalArgumentException if the answer type does not correspond with the field type.
     */
    public void setAnswer(String variable, long value) {
        FormField field = getField(variable);
        if (field == null) {
            throw new IllegalArgumentException("Field not found for the specified variable name.");
        }
        if (!FormField.TYPE_TEXT_MULTI.equals(field.getType())
            && !FormField.TYPE_TEXT_PRIVATE.equals(field.getType())
            && !FormField.TYPE_TEXT_SINGLE.equals(field.getType())) {
            throw new IllegalArgumentException("This field is not of type long.");
        }
        setAnswer(field, new Long(value));
    }

    /**
     * Sets a new float value to a given form's field. The field whose variable matches the 
     * requested variable will be completed with the specified value. If no field could be found 
     * for the specified variable then an exception will be raised.
     * 
     * @param variable the variable name that was completed.
     * @param value the float value that was answered.
     * @throws IllegalStateException if the form is not of type "submit".
     * @throws IllegalArgumentException if the form does not include the specified variable.
     * @throws IllegalArgumentException if the answer type does not correspond with the field type.
     */
    public void setAnswer(String variable, float value) {
        FormField field = getField(variable);
        if (field == null) {
            throw new IllegalArgumentException("Field not found for the specified variable name.");
        }
        if (!FormField.TYPE_TEXT_MULTI.equals(field.getType())
            && !FormField.TYPE_TEXT_PRIVATE.equals(field.getType())
            && !FormField.TYPE_TEXT_SINGLE.equals(field.getType())) {
            throw new IllegalArgumentException("This field is not of type float.");
        }
        setAnswer(field, new Float(value));
    }

    /**
     * Sets a new double value to a given form's field. The field whose variable matches the 
     * requested variable will be completed with the specified value. If no field could be found 
     * for the specified variable then an exception will be raised.
     * 
     * @param variable the variable name that was completed.
     * @param value the double value that was answered.
     * @throws IllegalStateException if the form is not of type "submit".
     * @throws IllegalArgumentException if the form does not include the specified variable.
     * @throws IllegalArgumentException if the answer type does not correspond with the field type.
     */
    public void setAnswer(String variable, double value) {
        FormField field = getField(variable);
        if (field == null) {
            throw new IllegalArgumentException("Field not found for the specified variable name.");
        }
        if (!FormField.TYPE_TEXT_MULTI.equals(field.getType())
            && !FormField.TYPE_TEXT_PRIVATE.equals(field.getType())
            && !FormField.TYPE_TEXT_SINGLE.equals(field.getType())) {
            throw new IllegalArgumentException("This field is not of type double.");
        }
        setAnswer(field, new Double(value));
    }

    /**
     * Sets a new boolean value to a given form's field. The field whose variable matches the 
     * requested variable will be completed with the specified value. If no field could be found 
     * for the specified variable then an exception will be raised.
     * 
     * @param variable the variable name that was completed.
     * @param value the boolean value that was answered.
     * @throws IllegalStateException if the form is not of type "submit".
     * @throws IllegalArgumentException if the form does not include the specified variable.
     * @throws IllegalArgumentException if the answer type does not correspond with the field type.
     */
    public void setAnswer(String variable, boolean value) {
        FormField field = getField(variable);
        if (field == null) {
            throw new IllegalArgumentException("Field not found for the specified variable name.");
        }
        if (!FormField.TYPE_BOOLEAN.equals(field.getType())) {
            throw new IllegalArgumentException("This field is not of type boolean.");
        }
        setAnswer(field, (value ? "1" : "0"));
    }

    /**
     * Sets a new Object value to a given form's field. In fact, the object representation 
     * (i.e. #toString) will be the actual value of the field.<p>
     * 
     * If the value to set to the field is not a basic type (e.g. String, boolean, int, etc.) you
     * will need to use {@link #setAnswer(String, String))} where the String value is the 
     * String representation of the object.<p> 
     * 
     * Before setting the new value to the field we will check if the form is of type submit. If 
     * the form isn't of type submit means that it's not possible to complete the form and an   
     * exception will be thrown.
     * 
     * @param field the form field that was completed.
     * @param value the Object value that was answered. The object representation will be the 
     * actual value.
     * @throws IllegalStateException if the form is not of type "submit".
     */
    private void setAnswer(FormField field, Object value) {
        if (!isSubmitType()) {
            throw new IllegalStateException("Cannot set an answer if the form is not of type " +
            "\"submit\"");
        }
        field.resetValues();
        field.addValue(value.toString());
    }

    /**
     * Sets a new values to a given form's field. The field whose variable matches the requested 
     * variable will be completed with the specified values. If no field could be found for 
     * the specified variable then an exception will be raised.<p>
     * 
     * The Objects contained in the List could be of any type. The String representation of them
     * (i.e. #toString) will be actually used when sending the answer to the server.
     * 
     * @param variable the variable that was completed.
     * @param values the values that were answered.
     * @throws IllegalStateException if the form is not of type "submit".
     * @throws IllegalArgumentException if the form does not include the specified variable.
     */
    public void setAnswer(String variable, List values) {
        if (!isSubmitType()) {
            throw new IllegalStateException("Cannot set an answer if the form is not of type " +
            "\"submit\"");
        }
        FormField field = getField(variable);
        if (field != null) {
            // Check that the field can accept a collection of values
            if (!FormField.TYPE_JID_MULTI.equals(field.getType())
                && !FormField.TYPE_LIST_MULTI.equals(field.getType())
                && !FormField.TYPE_LIST_SINGLE.equals(field.getType())
                && !FormField.TYPE_HIDDEN.equals(field.getType())) {
                throw new IllegalArgumentException("This field only accept list of values.");
            }
            // Clear the old values 
            field.resetValues();
            // Set the new values. The string representation of each value will be actually used.
            field.addValues(values);
        }
        else {
            throw new IllegalArgumentException("Couldn't find a field for the specified variable.");
        }
    }

    /**
     * Sets the default value as the value of a given form's field. The field whose variable matches
     * the requested variable will be completed with its default value. If no field could be found
     * for the specified variable then an exception will be raised.
     *
     * @param variable the variable to complete with its default value.
     * @throws IllegalStateException if the form is not of type "submit".
     * @throws IllegalArgumentException if the form does not include the specified variable.
     */
    public void setDefaultAnswer(String variable) {
        if (!isSubmitType()) {
            throw new IllegalStateException("Cannot set an answer if the form is not of type " +
            "\"submit\"");
        }
        FormField field = getField(variable);
        if (field != null) {
            // Clear the old values
            field.resetValues();
            // Set the default value
            for (Iterator it = field.getValues(); it.hasNext();) {
                field.addValue((String) it.next());
            }
        }
        else {
            throw new IllegalArgumentException("Couldn't find a field for the specified variable.");
        }
    }

    /**
     * Returns an Iterator for the fields that are part of the form.
     *
     * @return an Iterator for the fields that are part of the form.
     */
    public Iterator getFields() {
        return dataForm.getFields();
    }

    /**
     * Returns the field of the form whose variable matches the specified variable.
     * The fields of type FIXED will never be returned since they do not specify a 
     * variable. 
     * 
     * @param variable the variable to look for in the form fields. 
     * @return the field of the form whose variable matches the specified variable.
     */
    public FormField getField(String variable) {
        if (variable == null || variable.equals("")) {
            throw new IllegalArgumentException("Variable must not be null or blank.");
        }
        // Look for the field whose variable matches the requested variable
        FormField field;
        for (Iterator it=getFields();it.hasNext();) {
            field = (FormField)it.next();
            if (variable.equals(field.getVariable())) {
                return field;
            }
        }
        return null;
    }

    /**
     * Returns the instructions that explain how to fill out the form and what the form is about.
     * 
     * @return instructions that explain how to fill out the form.
     */
    public String getInstructions() {
        StringBuffer sb = new StringBuffer();
        // Join the list of instructions together separated by newlines
        for (Iterator it = dataForm.getInstructions(); it.hasNext();) {
            sb.append((String) it.next());
            // If this is not the last instruction then append a newline
            if (it.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }


    /**
     * Returns the description of the data. It is similar to the title on a web page or an X 
     * window.  You can put a <title/> on either a form to fill out, or a set of data results.
     * 
     * @return description of the data.
     */
    public String getTitle() {
        return dataForm.getTitle();
    }


    /**
     * Returns the meaning of the data within the context. The data could be part of a form
     * to fill out, a form submission or data results.<p>
     * 
     * Possible form types are:
     * <ul>
     *  <li>form -> Indicates a form to fill out.</li>
     *  <li>submit -> The form is filled out, and this is the data that is being returned from 
     * the form.</li>
     *  <li>cancel -> The form was cancelled. Tell the asker that piece of information.</li>
     *  <li>result -> Data results being returned from a search, or some other query.</li>
     * </ul>
     * 
     * @return the form's type.
     */
    public String getType() {
        return dataForm.getType(); 
    }
    

    /**
     * Sets instructions that explain how to fill out the form and what the form is about.
     * 
     * @param instructions instructions that explain how to fill out the form.
     */
    public void setInstructions(String instructions) {
        // Split the instructions into multiple instructions for each existent newline
        ArrayList instructionsList = new ArrayList();
        StringTokenizer st = new StringTokenizer(instructions, "\n");
        while (st.hasMoreTokens()) {
            instructionsList.add(st.nextToken());
        }
        // Set the new list of instructions
        dataForm.setInstructions(instructionsList);
        
    }


    /**
     * Sets the description of the data. It is similar to the title on a web page or an X window.
     * You can put a <title/> on either a form to fill out, or a set of data results.
     * 
     * @param title description of the data.
     */
    public void setTitle(String title) {
        dataForm.setTitle(title);
    }
    
    /**
     * Returns a DataForm that serves to send this Form to the server. If the form is of type 
     * submit, it may contain fields with no value. These fields will be removed since they only 
     * exist to assist the user while editing/completing the form in a UI. 
     * 
     * @return the wrapped DataForm.
     */
    public DataForm getDataFormToSend() {
        if (isSubmitType()) {
            // Create a new DataForm that contains only the answered fields 
            DataForm dataFormToSend = new DataForm(getType());
            for(Iterator it=getFields();it.hasNext();) {
                FormField field = (FormField)it.next();
                if (field.getValues().hasNext()) {
                    dataFormToSend.addField(field);
                }
            }
            return dataFormToSend;
        }
        return dataForm;
    }
    
    /**
     * Returns true if the form is a form to fill out.
     * 
     * @return if the form is a form to fill out.
     */
    private boolean isFormType() {
        return TYPE_FORM.equals(dataForm.getType());
    }
    
    /**
     * Returns true if the form is a form to submit.
     * 
     * @return if the form is a form to submit.
     */
    private boolean isSubmitType() {
        return TYPE_SUBMIT.equals(dataForm.getType());
    }

    /**
     * Returns a new Form to submit the completed values. The new Form will include all the fields
     * of the original form except for the fields of type FIXED. Only the HIDDEN fields will 
     * include the same value of the original form. The other fields of the new form MUST be 
     * completed. If a field remains with no answer when sending the completed form, then it won't 
     * be included as part of the completed form.<p>
     * 
     * The reason why the fields with variables are included in the new form is to provide a model 
     * for binding with any UI. This means that the UIs will use the original form (of type 
     * "form") to learn how to render the form, but the UIs will bind the fields to the form of
     * type submit.
     * 
     * @return a Form to submit the completed values.
     */
    public Form createAnswerForm() {
        if (!isFormType()) {
            throw new IllegalStateException("Only forms of type \"form\" could be answered");
        }
        // Create a new Form
        Form form = new Form(TYPE_SUBMIT);
        for (Iterator fields=getFields(); fields.hasNext();) {
            FormField field = (FormField)fields.next();
            // Add to the new form any type of field that includes a variable.
            // Note: The fields of type FIXED are the only ones that don't specify a variable
            if (field.getVariable() != null) {
                FormField newField = new FormField(field.getVariable());
                newField.setType(field.getType());
                form.addField(newField);
                // Set the answer ONLY to the hidden fields 
                if (FormField.TYPE_HIDDEN.equals(field.getType())) {
                    // Since a hidden field could have many values we need to collect them 
                    // in a list
                    List values = new ArrayList();
                    for (Iterator it=field.getValues();it.hasNext();) {
                        values.add((String)it.next());
                    }
                    form.setAnswer(field.getVariable(), values);
                }                
            }
        }
        return form;
    }

}
