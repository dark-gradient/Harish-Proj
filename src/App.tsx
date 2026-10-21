/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useRef } from 'react';
import {
  BASE_LIST,
  ConversionResult,
  NUMBER_BASES,
  NumberBase,
  validateInput,
  convertNumber,
} from './converterEngine';

export default function App() {
  const [inputValue, setInputValue] = useState<string>('');
  const [sourceBase, setSourceBase] = useState<NumberBase>(NumberBase.DECIMAL);
  const [targetBase, setTargetBase] = useState<NumberBase>(NumberBase.BINARY);

  const [conversionResult, setConversionResult] = useState<ConversionResult | null>(null);
  const [errorState, setErrorState] = useState<{ message: string; guidance: string } | null>(null);
  const [stepsOutput, setStepsOutput] = useState<string>('');

  const stepsAreaRef = useRef<HTMLPreElement>(null);

  const handleConvert = () => {
    const validation = validateInput(inputValue, sourceBase);
    if (!validation.valid) {
      setConversionResult(null);
      setErrorState({
        message: validation.errorMessage,
        guidance: validation.detailedGuidance,
      });
      setStepsOutput(validation.detailedGuidance);
      if (stepsAreaRef.current) {
        stepsAreaRef.current.scrollTop = 0;
      }
      return;
    }

    const result = convertNumber(validation, sourceBase, targetBase);
    setErrorState(null);
    setConversionResult(result);
    setStepsOutput(result.stepByStepExplanation);
    if (stepsAreaRef.current) {
      stepsAreaRef.current.scrollTop = 0;
    }
  };

  const currentSourceInfo = NUMBER_BASES[sourceBase];

  return (
    <div className="w-full h-screen overflow-hidden bg-[#F2F0EC] text-[#282522] flex flex-col">
      {/* Compact Header Bar — IBM Plex Sans SemiBold */}
      <header className="shrink-0 bg-[#F2F0EC] border-b border-[#D6D0C8] px-4 py-3">
        <h1 className="text-[18px] font-semibold text-[#282522] leading-snug text-left">
          Number System Converter
        </h1>
      </header>

      {/* Flat Input Controls Section */}
      <section className="shrink-0 bg-[#FCFBF8] border-b border-[#D6D0C8] px-4 py-3">
        {/* Row 0 & Row 1: Number Input Field + Allowed Digits Hint */}
        <div className="grid grid-cols-1 sm:grid-cols-12 gap-x-3 items-baseline">
          <label
            htmlFor="number-input"
            className="sm:col-span-1 text-[13px] font-medium text-[#282522] whitespace-nowrap text-left"
          >
            Number:
          </label>
          <div className="sm:col-span-11">
            <input
              id="number-input"
              type="text"
              value={inputValue}
              onChange={(e) => {
                setInputValue(e.target.value);
                if (errorState) {
                  setErrorState(null);
                }
              }}
              onKeyDown={(e) => {
                if (e.key === 'Enter') {
                  handleConvert();
                }
              }}
              aria-invalid={errorState ? 'true' : 'false'}
              aria-describedby="allowed-digits-hint"
              className={`w-full h-[34px] text-[14px] font-normal text-[#282522] bg-[#FCFBF8] px-2.5 py-1 border ${
                errorState
                  ? 'border-2 border-[#8C2F39]'
                  : 'border-[#D6D0C8] hover:border-[#6D665E]'
              } focus:outline-none focus:border-2 focus:border-[#76283A]`}
            />
            <div
              id="allowed-digits-hint"
              className="text-[12px] font-normal text-[#6D665E] mt-1 leading-normal text-left"
            >
              Allowed digits for {currentSourceInfo.displayName}: {currentSourceInfo.allowedDigitsDescription}
            </div>
          </div>
        </div>

        {/* Row 2: From Base, To Base, and Convert Button on the same row */}
        <div className="grid grid-cols-1 sm:grid-cols-12 gap-x-3 gap-y-2 items-center mt-2">
          <label
            htmlFor="source-base-select"
            className="sm:col-span-1 text-[13px] font-medium text-[#282522] whitespace-nowrap text-left"
          >
            From Base:
          </label>
          <div className="sm:col-span-4">
            <select
              id="source-base-select"
              value={sourceBase}
              onChange={(e) => setSourceBase(e.target.value as NumberBase)}
              className="w-full h-[34px] text-[14px] font-medium text-[#282522] bg-[#FCFBF8] border border-[#D6D0C8] hover:border-[#6D665E] px-2 focus:outline-none focus:border-2 focus:border-[#76283A] cursor-pointer"
            >
              {BASE_LIST.map((b) => (
                <option key={b.id} value={b.id} className="bg-[#FCFBF8] text-[#282522] font-normal">
                  {b.displayName}
                </option>
              ))}
            </select>
          </div>

          <label
            htmlFor="target-base-select"
            className="sm:col-span-1 text-[13px] font-medium text-[#282522] sm:pl-2 whitespace-nowrap text-left"
          >
            To Base:
          </label>
          <div className="sm:col-span-4">
            <select
              id="target-base-select"
              value={targetBase}
              onChange={(e) => setTargetBase(e.target.value as NumberBase)}
              className="w-full h-[34px] text-[14px] font-medium text-[#282522] bg-[#FCFBF8] border border-[#D6D0C8] hover:border-[#6D665E] px-2 focus:outline-none focus:border-2 focus:border-[#76283A] cursor-pointer"
            >
              {BASE_LIST.map((b) => (
                <option key={b.id} value={b.id} className="bg-[#FCFBF8] text-[#282522] font-normal">
                  {b.displayName}
                </option>
              ))}
            </select>
          </div>

          <div className="sm:col-span-2">
            <button
              type="button"
              onClick={handleConvert}
              className="w-full h-[34px] px-4 text-[13px] font-medium text-[#FCFBF8] bg-[#76283A] border border-[#76283A] hover:bg-[#632030] active:bg-[#521A27] focus:outline-none focus-visible:ring-2 focus-visible:ring-[#282522] focus-visible:ring-offset-1 focus-visible:ring-offset-[#FCFBF8] disabled:bg-[#D6D0C8] disabled:text-[#6D665E] disabled:border-[#D6D0C8] cursor-pointer whitespace-nowrap"
            >
              Convert
            </button>
          </div>
        </div>
      </section>

      {/* Flat Result Section — Heading in IBM Plex Sans SemiBold, Result in IBM Plex Mono Medium */}
      <section
        aria-live="polite"
        className={`shrink-0 border-b border-[#D6D0C8] px-4 py-2.5 ${
          errorState ? 'bg-[#F3E8EB]' : 'bg-[#F2F0EC]'
        }`}
      >
        <div className="flex items-baseline justify-between gap-4">
          <span
            className={`text-[13px] font-semibold text-left ${
              errorState ? 'text-[#8C2F39]' : 'text-[#282522]'
            }`}
          >
            {errorState
              ? 'Validation Error'
              : conversionResult
              ? `Result  —  (${conversionResult.normalizedInput})${
                  NUMBER_BASES[conversionResult.sourceBase].subscript
                } to ${NUMBER_BASES[conversionResult.targetBase].displayName}`
              : 'Result'}
          </span>
          {!errorState && conversionResult && (
            <span className="text-[12px] font-normal text-[#6D665E] whitespace-nowrap">
              Base {NUMBER_BASES[conversionResult.targetBase].radix}
            </span>
          )}
        </div>

        <div className="mt-0.5 min-h-[26px] flex items-center overflow-x-auto">
          {errorState ? (
            <span role="alert" className="text-[13px] font-medium text-[#8C2F39] text-left">
              Error: {errorState.message}
            </span>
          ) : conversionResult ? (
            <span className="font-mono text-[18px] font-medium text-[#282522] break-all select-all text-left">
              {conversionResult.convertedOutput}
            </span>
          ) : null}
        </div>
      </section>

      {/* Step-by-Step Mathematical Calculations Area — Heading in IBM Plex Sans SemiBold, Steps in IBM Plex Mono Regular */}
      <main className="flex-1 min-h-[180px] bg-[#F2F0EC] px-4 pt-2.5 pb-3.5 flex flex-col overflow-hidden">
        <div className="mb-1.5 shrink-0">
          <h2 className="text-[13px] font-semibold text-[#282522] text-left">
            Step-by-Step Mathematical Calculations
          </h2>
        </div>
        <pre
          ref={stepsAreaRef}
          tabIndex={0}
          aria-label="Step-by-Step Mathematical Calculations"
          className="flex-1 min-h-0 w-full overflow-auto bg-[#FCFBF8] border border-[#D6D0C8] focus:outline-none focus:border-2 focus:border-[#76283A] p-3 text-[13px] leading-relaxed font-mono font-normal text-[#282522] select-all text-left m-0"
        >
          {stepsOutput}
        </pre>
      </main>
    </div>
  );
}
